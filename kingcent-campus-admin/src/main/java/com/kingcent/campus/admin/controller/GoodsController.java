package com.kingcent.campus.admin.controller;

import com.kingcent.campus.common.entity.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/goods")
public class GoodsController {

    //用网关访问这个接口，能够打通就说明成功了
    //注意：这个链接接口我在gateway（网关）的配置里加了白名单才能打通的，删除这个接口时记得把白名单的也删一下
    @RequestMapping("/test")
    public Result<?> test(){
        return Result.success("测试成功");
    }
}
