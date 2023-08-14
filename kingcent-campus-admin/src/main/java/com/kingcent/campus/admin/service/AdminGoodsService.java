package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.constant.GoodsSortType;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsDetailsVo;
import com.kingcent.campus.shop.entity.vo.goods.GoodsVo;
import com.kingcent.campus.shop.mapper.GoodsMapper;
import com.kingcent.campus.shop.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzy
 */
@Service
@Slf4j
public class AdminGoodsService extends ServiceImpl<GoodsMapper, GoodsEntity> implements GoodsService {


    @Override
    public List<GoodsVo> getGoodsList(Long groupId, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public List<GoodsVo> searchGoods(Long groupId, String key, Integer page, Integer pageSize, Long categoryId, GoodsSortType sortType, Boolean deliveryToday, Boolean freeForDelivery) {
        return null;
    }

    @Override
    public GoodsDetailsVo details(Long goodsId) {
        return null;
    }

}
