package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.GoodsCommentEntity;
import com.kingcent.campus.entity.vo.goods.GoodsDetailsCommentsPreviewVo;

public interface GoodsCommentService extends IService<GoodsCommentEntity> {
    GoodsDetailsCommentsPreviewVo getGoodsDetailsCommentsPreview(Long spuId);
}
