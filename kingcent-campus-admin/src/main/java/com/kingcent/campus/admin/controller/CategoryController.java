package com.kingcent.campus.admin.controller;

import com.kingcent.campus.admin.service.CategoryService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.shop.entity.vo.CategoryVo;
import com.kingcent.campus.shop.entity.vo.category.CreateCategoryVo;
import com.kingcent.campus.shop.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zzy
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    /**
     * 查询商品分类列表
     * @param parentId  父节点id
     */
    @GetMapping("/list/{parentId}")
    public Result<List<CategoryVo>> list(
            @PathVariable Long parentId,
            @RequestParam(required = false) Integer height,
            @RequestParam(required = false) Boolean withPrice,
            @RequestParam(required = false) Boolean withSales
    ) {
        List<CategoryVo> categoryVos = categoryService.get(parentId, height, withPrice, withSales);
        return Result.success(categoryVos);
    }

    @PostMapping("/create/{parentId}")
    public Result<?> create(@PathVariable Long parentId, @RequestBody CreateCategoryVo vo){
        if (vo.getName() == null || vo.getName().trim().equals("")) return Result.fail("name不能为空");
        if(parentId != 0 && categoryService.getById(parentId) == null){
            return Result.fail("父分类标题不存在");
        }

        CategoryEntity category = BeanCopyUtils.copyBean(vo, CategoryEntity.class);
        category.setCreateTime(LocalDateTime.now());
        category.setParentId(parentId);
        categoryService.save(category);
        return Result.success();
    }

    @PutMapping("/update/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody CreateCategoryVo vo){
        if(categoryService.getById(id) == null){
            return Result.fail("分类标题不存在");
        }
        CategoryEntity category = BeanCopyUtils.copyBean(vo, CategoryEntity.class);
        category.setId(id);
        category.setCreateTime(null);
        category.setUpdateTime(LocalDateTime.now());
        categoryService.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> update(@PathVariable Long id){
        if(categoryService.getById(id) == null){
            return Result.fail("分类标题不存在");
        }
        categoryService.removeById(id);
        return Result.success();
    }

}