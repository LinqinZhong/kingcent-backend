package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.GoodsEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.purchase.PurchaseInfoVo;

public interface GoodsService extends IService<GoodsEntity> {
    Result<PurchaseInfoVo> getPurchaseInfo(Long userId, Long goodsId, String specInfo, Integer count);
}
