package com.kingcent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.shop.entity.GoodsSpecValueEntity;
import com.kingcent.common.shop.mapper.GoodsSpecValueMapper;
import com.kingcent.service.GoodsSpecValueService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class AppGoodsSpecValService extends ServiceImpl<GoodsSpecValueMapper, GoodsSpecValueEntity> implements GoodsSpecValueService {
}
