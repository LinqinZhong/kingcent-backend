package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单退款映射表
 * @author rainkyzhong
 * @date 2023/8/19 5:44
 */
@TableName("kc_shop_order_refund_map")
@Data
@AllArgsConstructor
public class OrderRefundMapEntity {
    private Long refundId;
    private Long orderId;
}
