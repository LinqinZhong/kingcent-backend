package com.kingcent.common.shop.entity.vo.purchase;

import lombok.Data;

@Data
public class QueryPurchaseVo {
    private Long goodsId;
    private String specInfo;
    private Integer count;
}
