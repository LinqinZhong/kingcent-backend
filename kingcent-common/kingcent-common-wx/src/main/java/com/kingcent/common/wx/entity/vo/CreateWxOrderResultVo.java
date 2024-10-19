package com.kingcent.common.wx.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/9 1:01
 */
@Data
public class CreateWxOrderResultVo {
    private List<Long> orderIds;
    private WxPaymentInfoVo wxPaymentInfo;
    //多订单模式：订单过多无法直接发起支付，需要买家单个支付
    private Boolean isMultiOrder;
}
