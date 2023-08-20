package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单退款信息
 * @author rainkyzhong
 * @date 2023/8/19 5:44
 */
@TableName("kc_shop_order_refund")
@Data
public class OrderRefundEntity {
    private Long orderId;
    private Integer reason;
    private String message;
    private LocalDateTime createTime;
    private LocalDateTime refundTime;
}
