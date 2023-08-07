package com.kingcent.campus.shop.entity.vo.goods;

import com.kingcent.campus.shop.entity.GoodsEntity;
import lombok.Data;

import java.util.List;

@Data
public class GoodsPageVo {
    private List<GoodsEntity> list;
    private Long total;
    private Integer current;
}
