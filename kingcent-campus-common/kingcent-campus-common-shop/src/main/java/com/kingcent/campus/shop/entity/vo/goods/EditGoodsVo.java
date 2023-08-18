package com.kingcent.campus.shop.entity.vo.goods;

import lombok.Data;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/14 18:36
 */
@Data
public class EditGoodsVo {
    private String name;
    private String thumbnail;
    private List<String> images;
    private List<String> description;
}
