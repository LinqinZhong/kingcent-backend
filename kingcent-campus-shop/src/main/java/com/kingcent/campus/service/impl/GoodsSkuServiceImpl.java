package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.GoodsSkuEntity;
import com.kingcent.campus.mapper.GoodsSkuMapper;
import com.kingcent.campus.service.GoodsSkuService;
import org.springframework.stereotype.Service;

@Service
public class GoodsSkuServiceImpl extends ServiceImpl<GoodsSkuMapper, GoodsSkuEntity>  implements GoodsSkuService {
}