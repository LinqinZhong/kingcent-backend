package com.kingcent.campus.shop.entity.vo.goods;

import com.kingcent.campus.shop.entity.GoodsCategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/14 18:36
 */
@Data
public class GoodsInfoVo {
    private Long id;
    private Long shopId;
    private String shopName;
    private String name;
    private String thumbnail;
    private String images;
    private String description;
    private String categoryIds;
    private String categoryNames;
}
