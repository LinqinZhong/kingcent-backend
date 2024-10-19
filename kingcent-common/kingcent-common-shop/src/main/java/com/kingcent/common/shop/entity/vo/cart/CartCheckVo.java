package com.kingcent.common.shop.entity.vo.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartCheckVo {
    private List<String> cartGoodsCodes;
    private Boolean checked;
}