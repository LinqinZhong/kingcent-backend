package com.kingcent.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.user.entity.UserInfoEntity;
import com.kingcent.common.shop.entity.OrderEntity;
import com.kingcent.common.shop.entity.vo.UserCenterVo;
import com.kingcent.service.OrderService;
import com.kingcent.service.UserInfoService;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user_info")
public class UserInfoController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserInfoService userInfoService;


    @GetMapping("/center")
    @ResponseBody
    public Result<UserCenterVo> getUserCenter(HttpServletRequest request){
        Long userId = RequestUtil.getUserId(request);
        UserInfoEntity user = userInfoService.get(userId);
        UserCenterVo center = new UserCenterVo();
        if (user != null) {
            center.setNickname(user.getNickname());
            center.setAvatarUrl(user.getAvatarUrl());
        }
        long countToPay = orderService.count(
                new QueryWrapper<OrderEntity>()
                        .eq("user_id", userId)
                        .eq("status", 0)
        );
        long countToDelivery = orderService.count(
                new QueryWrapper<OrderEntity>()
                        .eq("user_id", userId)
                        .eq("status", 1)
        );
        long countToReview = orderService.count(
                new QueryWrapper<OrderEntity>()
                        .eq("user_id", userId)
                        .eq("status", 3)
        );
        center.setCountOrderAfterSales(0);
        center.setCountOrderToDelivery((int) countToDelivery);
        center.setCountOrderToPay((int) countToPay);
        center.setCountOrderToReview((int) countToReview);
        return Result.success(center);
    }
}
