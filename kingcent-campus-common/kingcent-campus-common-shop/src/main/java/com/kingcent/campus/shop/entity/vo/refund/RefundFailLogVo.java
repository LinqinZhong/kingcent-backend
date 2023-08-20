package com.kingcent.campus.shop.entity.vo.refund;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2023/8/20 6:13
 */
@Data
@AllArgsConstructor
public class RefundFailLogVo {
    private String message;
    private LocalDateTime time;
    private Integer type;
}
