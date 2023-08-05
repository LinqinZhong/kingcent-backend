package com.kingcent.campus.service;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.purchase.PurchaseInfoVo;
import com.kingcent.campus.entity.vo.purchase.QueryPurchaseVo;

import java.util.List;

public interface PurchaseService {
    Result<PurchaseInfoVo> getPurchaseInfo(Long userId, List<QueryPurchaseVo> queries);
}
