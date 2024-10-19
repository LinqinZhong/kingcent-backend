package com.kingcent.common.shop.entity.vo.goods;

import lombok.Data;

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
