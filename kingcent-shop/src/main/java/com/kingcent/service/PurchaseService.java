package com.kingcent.service;

import com.kingcent.common.entity.result.Result;
import com.kingcent.common.shop.entity.vo.purchase.CheckPurchaseVo;
import com.kingcent.common.shop.entity.vo.purchase.PurchaseInfoVo;


public interface PurchaseService {
    Result<PurchaseInfoVo> getPurchaseInfo(Long userId, CheckPurchaseVo check);
}
