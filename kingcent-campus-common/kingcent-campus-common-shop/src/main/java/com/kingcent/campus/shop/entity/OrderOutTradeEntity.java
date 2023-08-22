package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/8/23 5:12
 */
@Data
@TableName("kc_shop_order_out_trade")
public class OrderOutTradeEntity {
    private Long id;
    private String payType;
    private String outTradeNo;
    private Integer orderTotal;
    private String paymentPackage;
}
