package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.admin.entity.vo.EditSpecValVo;
import com.kingcent.campus.admin.entity.vo.EditSpecVo;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.GoodsSpecEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsSpecVo;

import java.util.Collection;
import java.util.List;

public interface GoodsSpecService extends IService<GoodsSpecEntity> {
    Result<List<GoodsSpecVo>> getSpecList(Long shopId, Long goodsId);

    Result<?> update(Long shopId, Long goodsId, Long specId, EditSpecVo vo);

    Result<?> create(Long shopId, Long goodsId, EditSpecVo vo);

    Result<?> delete(Long shopId, Long goodsId, Long specId);

    boolean batchDelete(Long shopId, Long goodsId, Collection<Long> specIds);
}
