package com.kingcent.common.shop.entity.vo.purchase;

import lombok.Data;

import java.util.List;

@Data
public class PutCartGoodsVo {
    private Long goodsId;
    private Integer count;
    private List<List<Integer>> sku;
}
