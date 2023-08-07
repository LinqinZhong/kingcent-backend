package com.kingcent.campus.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.GoodsCommentEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsDetailsCommentsPreviewVo;

public interface GoodsCommentService extends IService<GoodsCommentEntity> {
    GoodsDetailsCommentsPreviewVo getGoodsDetailsCommentsPreview(Long spuId);
}
