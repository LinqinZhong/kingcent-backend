package com.kingcent.campus.user.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.user.entity.UserInfoEntity;
import com.kingcent.campus.user.utils.RequestUtil;
import com.kingcent.campus.user.service.UserInfoService;
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
