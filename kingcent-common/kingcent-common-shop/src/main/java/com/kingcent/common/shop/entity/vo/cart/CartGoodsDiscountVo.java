package com.kingcent.common.shop.entity.vo.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartGoodsDiscountVo {
    //阈值（超过这个值，且这个值为超过的值中是最大时触发此折扣）
    private BigDecimal moreThan;
    //折扣类型[1按数量满减， 2按数量满折，3按数量满减，4按数量满折]
    private Integer type;
    //参考数值
    private BigDecimal num;
    //截至时间
    private LocalDateTime deadline;
}
