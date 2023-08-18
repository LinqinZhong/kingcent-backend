package com.kingcent.campus.service;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.shop.entity.vo.payment.WxPaymentInfo;

import java.time.LocalDateTime;

/**
 * 微信支付服务
 * @author rainkyzhong
 * @date 2023/8/18 13:09
 */
public interface WxPayService {

    WxPaymentInfo requestToPay(
            String openId,
            String outTradeNo,
            String goodsInfo,
            Long payPrice,
            String ipAddress,
            LocalDateTime orderCreateTime
    );

    String notify(String xmlData);
}
