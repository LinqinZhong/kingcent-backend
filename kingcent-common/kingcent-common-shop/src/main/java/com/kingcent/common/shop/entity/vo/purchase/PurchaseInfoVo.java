package com.kingcent.common.shop.entity.vo.purchase;

import com.kingcent.common.shop.entity.vo.address.AddressVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消费信息
 */
@Data
public class PurchaseInfoVo {
    private List<PurchaseStoreVo> storeList;
    private List<AddressVo> addressList;
    private Long addressId;

    //当前时间，用于给前端计算时间（避免前后端时间不同出现的时间错误）
    private LocalDateTime time;
    private BigDecimal discountPrice;
}
