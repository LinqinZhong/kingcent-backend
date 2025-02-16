package com.kingcent.auth.controller;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.auth.config.LoginConfig;
import com.kingcent.auth.dto.PasswordLoginDto;
import com.kingcent.auth.servcice.UserService;
import com.kingcent.auth.utils.DESUtil;
import com.kingcent.auth.vo.PasswordLoginVo;
import com.kingcent.auth.vo.PublicKeyVo;
import com.kingcent.auth.vo.WxLoginVo;
import com.kingcent.auth.servcice.UserLoginService;
import com.kingcent.auth.utils.IpUtil;
import com.kingcent.auth.utils.RSAUtil;
import com.kingcent.auth.utils.SecretUtil;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserEntity;
import com.kingcent.common.user.entity.UserLoginEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {


    @Autowired
    private LoginConfig loginConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;

    @Value("wx78ac22e61f72cba5")
    private String WX_APP_ID;

    @Value("759674b8d91f7c213483fcc1282b002f")
    private String WX_SECRET;

    private RestTemplate restTemplate;


    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }


    /**
     * 验证用户身份，验证成功返回用户id，失败返回null
     */
    @PostMapping("/check")
    public Long check(@RequestBody String token){
        return userLoginService.check(token);
    }


    // 私钥，暂时存这里，应该用redis存
    private final Map<UUID,String> privateKeys = new HashMap<>();

    @GetMapping("/key")
    public Result<PublicKeyVo> getPublicKey() throws NoSuchAlgorithmException {
        String[] keys = RSAUtil.genKeyPair();
        UUID uuid = UUID.randomUUID();
        privateKeys.put(uuid, keys[1]);
        return Result.success(new PublicKeyVo(uuid, keys[0]));
    }

    @PostMapping("/password")
    @Transactional
    public Result<PasswordLoginVo> passwordLogin(
            HttpServletRequest request,
            @RequestBody PasswordLoginDto loginDto
            ) throws Exception {
        String privateKey = privateKeys.get(loginDto.getUuid());
        if(privateKey == null){
            return Result.fail("系统错误");
        }
        String info = RSAUtil.decrypt(loginDto.getInfo(),privateKey);
        String username;
        String password;
        String key;
        try{
            JSONObject jsonObject = JSONObject.parseObject(info);
            username = jsonObject.getString("username");
            password = jsonObject.getString("password");
            key = jsonObject.getString("key");

        }catch (Exception e){
            return Result.fail("系统错误");
        }


        log.info("password:{},username{}",password,username);
        UserEntity user = userService.getOne(new QueryWrapper<UserEntity>()
                .eq("username", username)
        );

        System.out.println("登录");
        if (user == null)
            return Result.fail("用户不存在");
        //验证密码
        String realPassword = MD5.create().digestHex("password="+password+ "&salt="+ user.getPasswordSalt());
        String secret = MD5Utils.md5Hex((System.currentTimeMillis()+"").getBytes());
        if(realPassword.equals(user.getPassword())){
            //保存登录记录
            UserLoginEntity login = new UserLoginEntity();
            login.setCreateTime(LocalDateTime.now());
            login.setUserId(user.getId());
            login.setSecret(secret);
            login.setIp(IpUtil.getIpAddress(request));
            login.setIsDeleted(false);
            if(!userLoginService.save(login)) {
                //TODO 这里应该抛出一个特定的异常
                throw new RuntimeException("服务器错误");
            }

            JSONObject tokenInfo = new JSONObject();
            tokenInfo.put("uid", user.getId());
            tokenInfo.put("lid", login.getId());
            tokenInfo.put("type", "password");
            tokenInfo.put("secret",secret);
            tokenInfo.put("expired",System.currentTimeMillis()+864000000);
            String token = DESUtil.encrypt(loginConfig.TOKEN_KEY,tokenInfo.toJSONString());
            System.out.println(token);
            return Result.success(new PasswordLoginVo(DESUtil.encrypt(key,token)));
        }
        return Result.fail("密码错误");
    }

    @PostMapping("/wx")
    @Transactional
    public Result<WxLoginVo> wxLogin(
            HttpServletRequest request,
            @RequestParam String code,
            @RequestParam String key
    ) throws Exception {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+ WX_APP_ID +"&secret="+ WX_SECRET +"&js_code="+code+"&grant_type=authorization_code";
        JSONObject result = restTemplate.getForObject(url, JSONObject.class);
        if(result == null){
            return Result.fail("微信服务器无相应");
        }
        if(result.containsKey("openid")){
            String openid = result.getString("openid");
            if(openid == null) return Result.fail("获取信息为空");
            //获取用户
            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("wx_openid",openid));
            if(userEntity == null){
                //用户不存在，创建用户
                userEntity = new UserEntity();
                userEntity.setCreateTime(LocalDateTime.now());
                userEntity.setWxOpenid(openid);
            }
            //更新
            userEntity.setUpdateTime(LocalDateTime.now());
            userService.saveOrUpdate(userEntity);
            //计算secret
            String secret = SecretUtil.get(userEntity.getId(), code);
            //加密secret
            log.info("{}",key);
            String encryptedSecret = RSAUtil.encrypt(secret,key);
            //保存登录记录
            UserLoginEntity login = new UserLoginEntity();
            login.setSecret(secret);
            login.setCreateTime(LocalDateTime.now());
            login.setUserId(userEntity.getId());
            login.setIp(IpUtil.getIpAddress(request));
            if(!userLoginService.save(login)) {
                //TODO 这里应该抛出一个特定的异常
                throw new RuntimeException("服务器错误");
            }
            return Result.success(new WxLoginVo(login.getId(), userEntity.getId(), encryptedSecret));
        }
        return Result.fail(result.containsKey("errmsg") ? result.getString("errmsg") : "未知错误");
    }
}
