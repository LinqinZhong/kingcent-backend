package com.kingcent.common.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单退款失败日志
 * @author rainkyzhong
 * @date 2023/8/19 5:44
 */
@TableName("kc_shop_order_refund_fail_log")
@Data
public class OrderRefundFailLogEntity {
    private Long id;
    private Long refundId;
    private String message;
    private LocalDateTime time;
    private Integer type;
}
