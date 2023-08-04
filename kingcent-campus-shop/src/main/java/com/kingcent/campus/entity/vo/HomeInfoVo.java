package com.kingcent.campus.entity.vo;

import com.kingcent.campus.entity.vo.goods.GoodsListTypeVo;
import lombok.Data;

import java.util.List;

@Data
public class HomeInfoVo {
    private List<String> swiper;
    private List<GoodsListTypeVo> tabList;
    private String activityImg;
}
