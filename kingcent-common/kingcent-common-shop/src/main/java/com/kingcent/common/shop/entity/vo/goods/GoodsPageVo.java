package com.kingcent.common.shop.entity.vo.goods;

import com.kingcent.common.shop.entity.GoodsEntity;
import lombok.Data;

import java.util.List;

@Data
public class GoodsPageVo {
    private List<GoodsEntity> list;
    private Long total;
    private Integer current;
}
