package com.kingcent.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.auth.entity.UserEntity;
import com.kingcent.auth.servcice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2023/8/18 17:13
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取微信用户的openid
     * @param userId 用户id
     */
    @GetMapping("/wx_openid/{userId}")
    @ResponseBody
    public String getWxOpenid(@PathVariable Long userId){
        UserEntity user = userService.getById(userId);
        if (user == null) return "";
        return user.getWxOpenid();
    }

    /**
     * 根据微信用户的openid获取id
     * @param openid 用户id
     */
    @GetMapping("/id_of_wx_openid/{openid}")
    @ResponseBody
    public Long getByWxOpenid(@PathVariable String openid){
        UserEntity user = userService.getOne(
                new QueryWrapper<UserEntity>()
                        .eq("wx_openid", openid)
        );
        if (user == null) return -1L;
        return user.getId();
    }
}
