package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.GoodsCommentEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsDetailsCommentsPreviewVo;
import com.kingcent.campus.shop.mapper.GoodsCommentMapper;
import com.kingcent.campus.shop.service.GoodsCommentService;

import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
public class GoodsCommentServiceImpl extends ServiceImpl<GoodsCommentMapper, GoodsCommentEntity> implements GoodsCommentService {
    @Override
    public GoodsDetailsCommentsPreviewVo getGoodsDetailsCommentsPreview(Long spuId) {
        return null;
    }
}