package com.kingcent.campus.shop.entity.vo.purchase;

import lombok.Data;

import java.util.List;

@Data
public class CheckPurchaseVo {
    private List<QueryPurchaseVo> list;
    private Long addressId;
}
