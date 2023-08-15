package com.kingcent.campus.service;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.purchase.CheckPurchaseVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseInfoVo;


public interface PurchaseService {
    Result<PurchaseInfoVo> getPurchaseInfo(Long userId, CheckPurchaseVo check);
}
