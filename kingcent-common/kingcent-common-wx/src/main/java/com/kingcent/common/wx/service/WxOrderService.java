package com.kingcent.common.wx.service;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.common.result.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 微信支付相关订单服务
 * 接入微信支付的订单实现此接口
 * @author rainkyzhong
 * @date 2023/8/20 3:21
 */
public interface WxOrderService {
    Result<?> onWxPayed(Long userId, String outTradeNo, String tradeNo, Integer totalFee, LocalDateTime payTime);

    JSONObject onWxRefundSuccess(String outRefundNo, String refundNo, LocalDateTime refundTime, BigDecimal total, BigDecimal refund);

    JSONObject onWxRefundFail(String outRefundNo, String message, LocalDateTime time);
}
