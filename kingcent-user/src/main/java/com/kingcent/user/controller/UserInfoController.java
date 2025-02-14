package com.kingcent.user.controller;

import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserInfoEntity;
import com.kingcent.common.user.utils.RequestUtil;
import com.kingcent.user.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/12/12 15:39
 */
@RestController
@RequestMapping("/user_info")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/get")
    public Result<UserInfoEntity> get(HttpServletRequest request){
        Long userId = RequestUtil.getUserId(request);
        return userInfoService.get(userId);
    }

    @PostMapping("/heads")
    public Result<List<UserInfoEntity>> heads(@RequestBody List<Long> userIds){
        return userInfoService.heads(userIds);
    }
}
