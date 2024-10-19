package com.kingcent.common.shop.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2023/8/24 20:00
 */
@TableName("kc_shop_order_delivery")
@Data
public class OrderDeliveryEntity {
    private Long id;
    private Long orderId;
    private Long carrierId;
    private Integer status;
    private BigDecimal commission;
    private LocalDateTime deliveryTime;
}
