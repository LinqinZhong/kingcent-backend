package com.kingcent.campus.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("kc_shop_delivery_group")
public class DeliveryGroup {
    private Long shopId;
    private Long groupId;
    private BigDecimal deliveryFee;
}
