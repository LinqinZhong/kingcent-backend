package com.kingcent.auth.controller;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.auth.entity.UserEntity;
import com.kingcent.auth.entity.UserLoginEntity;
import com.kingcent.auth.servcice.UserService;
import com.kingcent.auth.entity.vo.AdminLoginVo;
import com.kingcent.auth.entity.vo.WxLoginVo;
import com.kingcent.auth.servcice.UserLoginService;
import com.kingcent.auth.utils.IpUtil;
import com.kingcent.auth.utils.RSAUtil;
import com.kingcent.auth.utils.SecretUtil;
import com.kingcent.common.entity.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {


    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;

    @Value("wx78ac22e61f72cba5")
    private String WX_APP_ID;

    @Value("759674b8d91f7c213483fcc1282b002f")
    private String WX_SECRET;

    @Autowired
    private RestTemplate restTemplate;


    /**
     * 验证用户身份，验证成功返回用户id，失败返回null
     */
    @PostMapping("/check")
    public Long check(@RequestBody JSONObject object){
        return userLoginService.check(object);
    }


    @PostMapping("/password")
    @Transactional
    public Result<AdminLoginVo> passwordLogin(
            HttpServletRequest request,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String key
    ) throws Exception {
        UserEntity user = userService.getOne(new QueryWrapper<UserEntity>()
                .eq("username", username)
        );

        System.out.println("登录");
        if (user == null)
            return Result.fail("用户不存在");
        //验证密码
        if((MD5.create().digestHex("password="+password+ "&salt="+ user.getPasswordSalt())).equals(user.getPassword())){
            String code = MD5.create().digestHex(System.currentTimeMillis()+password);
            //计算secret
            String secret = SecretUtil.get(user.getId(), code);
            //加密secret
            String encryptedSecret = RSAUtil.encrypt(secret,key);
            //保存登录记录
            UserLoginEntity login = new UserLoginEntity();
            login.setSecret(secret);
            login.setCreateTime(LocalDateTime.now());
            login.setUserId(user.getId());
            login.setIp(IpUtil.getIpAddress(request));
            if(!userLoginService.save(login)) {
                //TODO 这里应该抛出一个特定的异常
                throw new RuntimeException("服务器错误");
            }

            return Result.success(
                    new AdminLoginVo(
                            user.getId(),
                            login.getId(),
                            code,
                            encryptedSecret
                    )
            );
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
                userEntity.setCreateTime(new Date());
                userEntity.setWxOpenid(openid);
            }
            //更新
            userEntity.setUpdateTime(new Date());
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
