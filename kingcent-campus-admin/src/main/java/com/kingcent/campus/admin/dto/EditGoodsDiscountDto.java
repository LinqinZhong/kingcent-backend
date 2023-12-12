package com.kingcent.campus.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2023/11/30 14:55
 */
@Data
public class EditGoodsDiscountDto {
    private Long id;
    private BigDecimal moreThan;
    private Integer type;
    private BigDecimal num;
    private LocalDateTime deadline;
}
