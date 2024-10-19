package com.kingcent.common.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/8/23 5:12
 */
@Data
@TableName("kc_shop_order_payment")
public class OrderPaymentEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String payType;
    private Integer orderTotal;
    private String paymentPackage;
}
