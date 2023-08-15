package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.GoodsService;
import com.kingcent.campus.shop.constant.GoodsSortType;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsDetailsVo;
import com.kingcent.campus.shop.entity.vo.goods.GoodsVo;
import com.kingcent.campus.shop.mapper.GoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzy
 */
@Service
@Slf4j
public class AdminGoodsService extends ServiceImpl<GoodsMapper, GoodsEntity> implements GoodsService {

}
