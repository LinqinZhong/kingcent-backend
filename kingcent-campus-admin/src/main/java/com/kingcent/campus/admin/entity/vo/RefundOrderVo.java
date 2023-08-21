package com.kingcent.campus.admin.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2023/8/21 9:56
 */
@Data
public class RefundOrderVo {
    private Long id;
    private Long userId;
    private String outRefundNo;
    private String tradeNo;
    private Integer status;
    private String payType;
    private Integer reason;
    private String message;
    private BigDecimal refund;
    private BigDecimal originTotal;
    private LocalDateTime createTime;
    private LocalDateTime refundTime;
}
