package com.kingcent.campus.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsDetailsVo;
import com.kingcent.campus.shop.entity.vo.goods.GoodsVo;

import java.util.List;

public interface GoodsService extends IService<GoodsEntity> {
    List<GoodsVo> getGoodsList(Long groupId, String key, Integer page, Integer pageSize);

    GoodsDetailsVo details(Long goodsId);

    void selectGoodsPage(Integer pageNum, Integer pageSize, GoodsEntity goodsEntity, CategoryEntity categoryEntity);

}
