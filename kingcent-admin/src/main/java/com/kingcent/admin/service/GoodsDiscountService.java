package com.kingcent.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.admin.dto.EditGoodsDiscountDto;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.GoodsDiscountEntity;

public interface GoodsDiscountService extends IService<GoodsDiscountEntity> {
    Result<VoList<GoodsDiscountEntity>> list(Long shopId, Long goodsId, Integer page, Integer pageSize);

    Result<?> save(Long shopId, Long goodsId, EditGoodsDiscountDto dto);

    Result<?> update(Long shopId, Long goodsId, Long discountId, EditGoodsDiscountDto dto);
}
