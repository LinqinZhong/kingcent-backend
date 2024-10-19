package com.kingcent.controller;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.common.wx.service.WxPayService;
import com.kingcent.common.wx.service.WxRefundService;
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

    @Autowired
    private WxRefundService refundService;

    /**
     * 接收微信支付回传的支付通知
     */
    @PostMapping(value = "/payment", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String paymentNotify(@RequestBody String xmlData){
        return wxPayService.notify(xmlData);
    }

    /**
     * 接收微信支付回传的退款处理通知
     */
    @PostMapping(value = "/refund")
    @ResponseBody
    public JSONObject refundNotify(@RequestBody JSONObject data){
        return refundService.notify(data);
    }
}
