package com.kingcent.campus.wx.service;

import com.kingcent.campus.common.entity.result.Result;

import java.time.LocalDateTime;

/**
 * 微信支付相关订单服务
 * 接入微信支付的订单实现此接口
 * @author rainkyzhong
 * @date 2023/8/20 3:21
 */
public interface WxOrderService {
    Result<?> onWxPayed(Long userId, String outTradeNo, String tradeNo, Integer totalFee, LocalDateTime payTime);
}
