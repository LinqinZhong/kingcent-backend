package com.kingcent.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.shop.entity.GoodsEntity;
import com.kingcent.common.shop.entity.vo.goods.GoodsInfoVo;

public interface GoodsService extends IService<GoodsEntity> {
    boolean exist(Long shopId, Long goodsId);

    Result<GoodsInfoVo> info(Long shopId, Long goodsId);

    Result<?> save(Long shopId, GoodsInfoVo vo);

    Result<?> update(Long goodsId, GoodsInfoVo vo);
}
