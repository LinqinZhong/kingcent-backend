package com.kingcent.campus.wx.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信订单商品
 * @author rainkyzhong
 * @date 2023/8/20 3:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxOrderGoodsEntity {
    private String merchantGoodsId;
    private String goodsName;
    private Long unitPrice;
    private Long refundAmount;
    private Integer refundQuantity;
}
