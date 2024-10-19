package com.kingcent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.shop.entity.GoodsCommentCountEntity;
import com.kingcent.common.shop.mapper.GoodsCommentCountMapper;
import com.kingcent.service.GoodsCommentCountService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class AppShopGoodsCommentCountService extends ServiceImpl<GoodsCommentCountMapper, GoodsCommentCountEntity> implements GoodsCommentCountService {
}
