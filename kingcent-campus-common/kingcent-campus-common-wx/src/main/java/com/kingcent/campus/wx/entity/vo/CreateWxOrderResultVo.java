package com.kingcent.campus.wx.entity.vo;

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
}
