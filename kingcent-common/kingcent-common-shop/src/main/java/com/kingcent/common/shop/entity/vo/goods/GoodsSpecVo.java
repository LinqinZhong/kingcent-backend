package com.kingcent.common.shop.entity.vo.goods;

import lombok.Data;

import java.util.List;

@Data
public class GoodsSpecVo {
    private Long specId;
    private String title;
    private List<GoodsSpecValueVo> specValueList;
}
