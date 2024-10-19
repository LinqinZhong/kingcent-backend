package com.kingcent.common.shop.entity.vo.goods;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GoodsDetailsCommentsPreviewVo {
    private Integer	badCount;
    private Integer	middleCount;
    private Integer	goodCount;
    private Integer	hasImageCount;
    private List<GoodsCommentVo> comments;
}
