package com.kingcent.campus.admin.controller;

import com.kingcent.campus.admin.service.AdminCategoryService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.cart.CategoryVo;
import com.kingcent.campus.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zzy
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public Result<?> listAllCategory(){
        List<CategoryVo> list = categoryService.listAllCategory();
        return Result.success(list);
    }


}
