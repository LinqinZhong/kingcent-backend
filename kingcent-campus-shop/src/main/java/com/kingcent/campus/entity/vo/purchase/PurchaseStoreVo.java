package com.kingcent.campus.entity.vo.purchase;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseStoreVo {
    private Long id;
    private String name;
    private BigDecimal deliveryFee;
    private BigDecimal discountPrice;
    private BigDecimal finalPrice;
    private List<PurchaseGoodsVo> goodsList;
    private String allowedAddress;
    private List<LocalDateTime> deliveryTimeOptions;
}
