package com.kingcent.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.admin.entity.vo.EditSpecValVo;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.shop.entity.GoodsSpecValueEntity;

import java.util.Collection;

public interface GoodsSpecValueService extends IService<GoodsSpecValueEntity> {
    Result<?> update(Long shopId, Long goodsId, Long specId, Long specValId, EditSpecValVo vo);

    Result<?> create(Long shopId, Long goodsId, Long specId, EditSpecValVo vo);

    Result<?> delete(Long shopId, Long goodsId, Long specId, Long specValId);

    boolean batchDelete(Long shopId, Long goodsId, Collection<Long> specValIds);
}
