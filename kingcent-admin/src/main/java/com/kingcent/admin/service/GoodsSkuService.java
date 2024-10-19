package com.kingcent.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.admin.entity.vo.EditSkuVo;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.GoodsSkuEntity;

public interface GoodsSkuService extends IService<GoodsSkuEntity> {
    Result<VoList<GoodsSkuEntity>> list(Long shopId, Long goodsId, Integer page, Integer pageSize);

    Result<?> delete(Long shopId, Long goodsId, Long skuId);

    Result<?> create(Long shopId, Long goodsId, EditSkuVo vo);

    Result<?> update(Long shopId, Long goodsId, Long skuId, EditSkuVo vo);
}
