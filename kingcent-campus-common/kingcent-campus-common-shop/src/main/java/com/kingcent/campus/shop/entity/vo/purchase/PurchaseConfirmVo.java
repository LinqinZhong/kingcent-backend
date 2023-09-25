package com.kingcent.campus.shop.entity.vo.purchase;

import lombok.Data;

import java.util.List;

/**
 * 提交购买实体
 * @author rainkyzhong
 * @date 2023/8/8 6:58
 */
@Data
public class PurchaseConfirmVo {
    private Long addressId;
    private Double payPrice;
    private List<PurchaseConfirmStoreVo> storeList;
}
