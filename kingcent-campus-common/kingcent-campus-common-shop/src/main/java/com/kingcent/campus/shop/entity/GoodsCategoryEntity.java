package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/8/14 6:43
 */
@Data
@TableName("kc_shop_goods_category")
public class GoodsCategoryEntity {
    private Long goodsId;
    private Long categoryId;
}
