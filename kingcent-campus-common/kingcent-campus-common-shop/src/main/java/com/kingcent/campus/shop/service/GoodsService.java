package com.kingcent.campus.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.constant.GoodsSortType;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsDetailsVo;
import com.kingcent.campus.shop.entity.vo.goods.GoodsVo;

import java.util.List;

public interface GoodsService extends IService<GoodsEntity> {

    List<GoodsVo> getGoodsList(Long groupId, Integer page, Integer pageSize);

    List<GoodsVo> searchGoods(Long groupId, String key, Integer page, Integer pageSize, Long categoryId, GoodsSortType sortType, Boolean deliveryToday, Boolean freeForDelivery);

    GoodsDetailsVo details(Long goodsId);
}
