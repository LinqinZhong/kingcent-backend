package com.kingcent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.shop.entity.GoodsCommentEntity;
import com.kingcent.common.shop.entity.vo.goods.GoodsDetailsCommentsPreviewVo;

public interface GoodsCommentService extends IService<GoodsCommentEntity> {
    GoodsDetailsCommentsPreviewVo getGoodsDetailsCommentsPreview(Long spuId);
}
