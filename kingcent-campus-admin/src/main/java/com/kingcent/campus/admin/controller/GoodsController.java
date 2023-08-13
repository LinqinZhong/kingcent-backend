package com.kingcent.campus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.geom.RectangularShape;


@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    //用网关访问这个接口，能够打通就说明成功了
    //注意：这个链接接口我在gateway（网关）的配置里加了白名单才能打通的，删除这个接口时记得把白名单的也删一下
    @RequestMapping("/test")
    public Result<?> test(){
        return Result.success("测试成功");
    }

    @GetMapping("/goodsList")
    public Result<?> goodsList(Integer pageNum, Integer pageSize, GoodsEntity goodsEntity, CategoryEntity categoryEntity) {
        goodsService.selectGoodsPage(pageNum, pageSize, goodsEntity, categoryEntity);
        return null;

    }
}
