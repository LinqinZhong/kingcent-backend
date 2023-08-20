package com.kingcent.campus.controller;

import com.kingcent.campus.wx.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 微信服务回调接口控制器
 * @author rainkyzhong
 * @date 2023/8/20 4:31
 */
@RequestMapping("/wx_notify")
@RestController
public class WxNotifyController {

    @Autowired
    private WxPayService wxPayService;

    /**
     * 接收微信支付回传的支付通知
     */
    @PostMapping(value = "/payment", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String paymentNotify(@RequestBody String xmlData){
        return wxPayService.notify(xmlData);
    }
}
