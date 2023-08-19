package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款订单
 * @author rainkyzhong
 * @date 2023/8/19 5:44
 */
@TableName("kc_shop_refund_order")
@Data
public class RefundOrderEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String outRefundNo;
    private Integer status;
    private BigDecimal price;
    private LocalDateTime createTime;
    private LocalDateTime refundTime;
    private Long userId;
}
