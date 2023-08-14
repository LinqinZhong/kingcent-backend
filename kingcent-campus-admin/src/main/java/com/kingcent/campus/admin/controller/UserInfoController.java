package com.kingcent.campus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.UserInfoEntity;
import com.kingcent.campus.shop.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zzy
 */
@RestController
@RequestMapping("/UserInfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/{id}")
    public Result getUser(@PathVariable Long id){
        return userInfoService.getUser(id);
    }

    @GetMapping
    public Result<List<UserInfoEntity>> list(){
        LambdaQueryWrapper<UserInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        //后续需要啥条件在加

        List<UserInfoEntity> list = userInfoService.list(queryWrapper);
        return Result.success(list);
    }
}
