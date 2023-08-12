package com.kingcent.campus.auth.controller;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.auth.entity.AdminEntity;
import com.kingcent.campus.auth.entity.AdminLoginEntity;
import com.kingcent.campus.auth.entity.UserLoginEntity;
import com.kingcent.campus.auth.entity.vo.AdminLoginVo;
import com.kingcent.campus.auth.entity.vo.WxLoginVo;
import com.kingcent.campus.auth.servcice.AdminLoginService;
import com.kingcent.campus.auth.servcice.AdminService;
import com.kingcent.campus.auth.servcice.UserLoginService;
import com.kingcent.campus.auth.servcice.UserService;
import com.kingcent.campus.auth.entity.UserEntity;
import com.kingcent.campus.auth.utils.IpUtil;
import com.kingcent.campus.auth.utils.SecretEncryptUtil;
import com.kingcent.campus.auth.utils.SecretUtil;
import com.kingcent.campus.common.entity.constant.LoginType;
import com.kingcent.campus.common.entity.result.Result;
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

    @Value("wx57b0af44083c56ef")
    private String WX_APP_ID;

    @Value("d016b48827dadcf1836bdcd64cea0687")
    private String WX_SECRET;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminLoginService adminLoginService;

    /**
     * 验证用户身份，验证成功返回用户id，失败返回null
     */
    @PostMapping("/check")
    public Long check(@RequestBody JSONObject object){

        String sign = object.getString("sign");
        String path = object.getString("path");
        String data = object.getString("data");
        LoginType loginType = object.getObject("login-type", LoginType.class);
        Long lid = object.getLong("lid");

        System.out.println(loginType);

        if(loginType == null) return null;

        switch (loginType){
            case ADMIN -> {
                AdminLoginEntity login = adminLoginService.getById(lid);
                //TODO 鉴权
                return login.getId();
            }
            case CUSTOMER -> {
                UserLoginEntity userLoginEntity = userLoginService.getById(lid);
                if (userLoginEntity == null){
                    return null;
                }
                String secret = userLoginEntity.getSecret();
                long timestamp = System.currentTimeMillis()/60000;
                if (sign.equals(getSign(userLoginEntity.getUserId(),secret,path,timestamp,data)) || sign.equals(getSign(userLoginEntity.getUserId(),secret,path,timestamp - 1,data))) return userLoginEntity.getUserId();
                return null;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * 计算签名
     */
    private String getSign(Long userId, String secret, String path, long timestamp, String data){
        String value = "uid="+userId+"&secret="+secret+"&path="+path+"&timestamp="+timestamp+"/"+data;
        String sign = MD5.create().digestHex(value.getBytes());
        log.info("计算签名：\n{}-->{}", value,sign);
        return sign;
    }


    @PostMapping("/admin")
    @Transactional
    public Result<AdminLoginVo> adminLogin(
            HttpServletRequest request,
            @RequestParam String username,
            @RequestParam String password
    ){
        AdminEntity admin = adminService.getOne(new QueryWrapper<AdminEntity>()
                .eq("username", username)
        );

        System.out.println("登录");
        if (admin == null)
            return Result.fail("用户不存在");
        //验证密码
        if((MD5.create().digestHex("password="+password+ "&salt="+ admin.getSalt())).equals(admin.getPassword())){
            String code = MD5.create().digestHex(System.currentTimeMillis()+"");
            //计算secret
            String secret = SecretUtil.get(admin.getId(), code);
            //加密secret
            String encryptedSecret = SecretEncryptUtil.encrypt(admin.getId(), secret, code);
            //保存登录记录
            AdminLoginEntity login = new AdminLoginEntity();
            login.setSecret(secret);
            login.setCreateTime(LocalDateTime.now());
            login.setAdminId(admin.getId());
            login.setIp(IpUtil.getIpAddress(request));
            if(!adminLoginService.save(login)) {
                //TODO 这里应该抛出一个特定的异常
                throw new RuntimeException("服务器错误");
            }

            return Result.success(
                    new AdminLoginVo(
                            admin.getId(),
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
    public Result<WxLoginVo> wxLogin(HttpServletRequest request, @RequestParam String code){
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
            String encryptedSecret = SecretEncryptUtil.encrypt(userEntity.getId(), secret, code);
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
