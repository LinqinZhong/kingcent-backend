package com.kingcent.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.shop.entity.CategoryEntity;
import com.kingcent.common.shop.entity.vo.CategoryVo;
import com.kingcent.common.shop.entity.vo.category.CreateCategoryVo;

import java.util.List;

public interface CategoryService extends IService<CategoryEntity>{

    List<CategoryVo> get(Long parentId, Integer height, Boolean withPrice, Boolean withSales);

    Result<?> save(Long parentId, CreateCategoryVo vo);

    Result<?> delete(Long id);

    Result<?> move(Long id, Integer weight);

    Result<?> resetParent(Long id, Long parentId);

    List<String> getNames(List<Long> categoryIds);
}