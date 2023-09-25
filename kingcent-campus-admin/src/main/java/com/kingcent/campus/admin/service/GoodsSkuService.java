package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.admin.entity.vo.EditSkuVo;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.GoodsSkuEntity;

import java.util.List;

public interface GoodsSkuService extends IService<GoodsSkuEntity> {
    Result<List<GoodsSkuEntity>> list(Long shopId, Long goodsId);

    Result<?> delete(Long shopId, Long goodsId, Long skuId);

    Result<?> create(Long shopId, Long goodsId, EditSkuVo vo);

    Result<?> update(Long shopId, Long goodsId, Long skuId, EditSkuVo vo);
}
