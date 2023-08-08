package com.kingcent.campus.shop.entity.vo.purchase;

import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/8/8 7:05
 */
@Data
public class PurchaseConfirmGoodsVo {
    private Long id;
    private String sku;
    private Integer count;
}
