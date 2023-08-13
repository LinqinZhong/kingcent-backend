package com.kingcent.campus.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.shop.entity.vo.cart.CategoryVo;

import java.util.List;

public interface CategoryService extends IService<CategoryEntity> {

    List<CategoryVo> listAllCategory();

}
