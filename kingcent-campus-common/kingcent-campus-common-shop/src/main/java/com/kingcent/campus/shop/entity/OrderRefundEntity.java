package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单退款信息
 * @author rainkyzhong
 * @date 2023/8/19 5:44
 */
@TableName("kc_shop_order_refund")
@Data
public class OrderRefundEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String outRefundNo;
    private String tradeNo;
    private String refundNo;
    private Integer status;
    private String payType;
    private Integer reason;
    private String message;
    private BigDecimal refund;
    private BigDecimal originTotal;
    private LocalDateTime createTime;
    private LocalDateTime refundTime;
}
