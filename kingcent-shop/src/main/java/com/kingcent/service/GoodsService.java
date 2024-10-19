package com.kingcent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.shop.constant.GoodsSortType;
import com.kingcent.common.shop.entity.GoodsEntity;
import com.kingcent.common.shop.entity.vo.goods.GoodsDetailsVo;
import com.kingcent.common.shop.entity.vo.goods.GoodsVo;

import java.util.List;

public interface GoodsService extends IService<GoodsEntity> {

    List<GoodsVo> getGoodsList(Long groupId, Integer page, Integer pageSize);

    List<GoodsVo> searchGoods(Long groupId, String key, Integer page, Integer pageSize, Long categoryId, GoodsSortType sortType, Boolean deliveryToday, Boolean freeForDelivery);

    GoodsDetailsVo details(Long goodsId);
}
