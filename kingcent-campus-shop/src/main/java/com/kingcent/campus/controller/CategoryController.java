package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.CategoryVo;
import com.kingcent.campus.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


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
}
