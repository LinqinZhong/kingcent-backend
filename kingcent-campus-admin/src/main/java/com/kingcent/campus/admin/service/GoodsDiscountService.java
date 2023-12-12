package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.admin.dto.EditGoodsDiscountDto;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.GoodsDiscountEntity;

public interface GoodsDiscountService extends IService<GoodsDiscountEntity> {
    Result<VoList<GoodsDiscountEntity>> list(Long shopId, Long goodsId, Integer page, Integer pageSize);

    Result<?> save(Long shopId, Long goodsId, EditGoodsDiscountDto dto);

    Result<?> update(Long shopId, Long goodsId, Long discountId, EditGoodsDiscountDto dto);
}
