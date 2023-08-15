package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.shop.entity.vo.CategoryVo;

import java.util.List;

public interface CategoryService extends IService<CategoryEntity>{

    List<CategoryVo> get(Long parentId, Integer height, Boolean withPrice, Boolean withSales);
}
