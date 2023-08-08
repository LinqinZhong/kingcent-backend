package com.kingcent.campus.shop.entity.vo.purchase;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/8 7:03
 */
@Data
public class PurchaseConfirmStoreVo {
    private Long id;
    private LocalDateTime deliveryTime;
    private String payType;
    private String remark;
    private List<PurchaseConfirmGoodsVo> goodsList;
}
