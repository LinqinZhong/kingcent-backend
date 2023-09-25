package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.constant.GoodsSortType;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsDetailsVo;
import com.kingcent.campus.shop.entity.vo.goods.GoodsVo;

import java.util.List;

public interface GoodsService extends IService<GoodsEntity> {
    boolean exist(Long shopId, Long goodsId);
}
