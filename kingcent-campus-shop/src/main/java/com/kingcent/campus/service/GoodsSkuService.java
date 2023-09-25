package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.GoodsSkuEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsSkuInfoVo;

public interface GoodsSkuService extends IService<GoodsSkuEntity> {
    GoodsSkuInfoVo fetchGoodsSkuInfo(Long goodsId);

    boolean updateStockQuantity(long skuId, int delta);

    boolean changeSafeStockQuantity(long skuId, int delta);

    boolean changeSafeStockQuantityBySpecInfo(long goodsId, String specInfo, int delta);
}
