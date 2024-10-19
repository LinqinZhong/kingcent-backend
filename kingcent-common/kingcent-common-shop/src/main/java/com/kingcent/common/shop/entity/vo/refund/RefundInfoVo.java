package com.kingcent.common.shop.entity.vo.refund;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/20 5:45
 */
@Data
public class RefundInfoVo {
    private String outRefundNo;
    private String refundNo;
    private BigDecimal price;
    private String payType;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime refundTime;
    private Integer reason;
    private String message;
    private List<RefundFailLogVo> failLogs;
}
