package com.kingcent.admin.controller;

import com.kingcent.admin.service.CategoryService;
import com.kingcent.common.result.Result;
import com.kingcent.common.shop.entity.CategoryEntity;
import com.kingcent.common.shop.entity.vo.CategoryVo;
import com.kingcent.common.shop.entity.vo.category.CreateCategoryVo;
import com.kingcent.common.shop.util.BeanCopyUtils;
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
    @GetMapping("/list")
    public Result<List<CategoryVo>> list(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Integer height,
            @RequestParam(required = false) Boolean withPrice,
            @RequestParam(required = false) Boolean withSales
    ) {
        List<CategoryVo> categoryVos = categoryService.get(parentId, height, withPrice, withSales);
        return Result.success(categoryVos);
    }

    @PostMapping("/save/{parentId}")
    public Result<?> create(@PathVariable Long parentId, @RequestBody CreateCategoryVo vo){
        return categoryService.save(parentId, vo);
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
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> update(@PathVariable Long id){
        return categoryService.delete(id);
    }

    @PutMapping("/move/{id}/{weight}")
    public Result<?> move(@PathVariable Long id, @PathVariable Integer weight){
        return categoryService.move(id, weight);
    }

    @PutMapping("/reset_parent/{id}/{parentId}")
    public Result<?> move(@PathVariable Long id, @PathVariable Long parentId){
        return categoryService.resetParent(id, parentId);
    }
}