package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.admin.entity.vo.EditSpecValVo;
import com.kingcent.campus.admin.entity.vo.EditSpecVo;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.GoodsSpecValueEntity;

public interface GoodsSpecValueService extends IService<GoodsSpecValueEntity> {
    Result<?> update(Long shopId, Long specValId, EditSpecValVo vo);

    Result<?> create(Long shopId, Long goodsId, Long specId, EditSpecValVo vo);

    Result<?> delete(Long shopId, Long goodsId, Long specId, Long specValId);
}
