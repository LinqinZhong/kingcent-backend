package com.kingcent.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.CategoryVo;
import com.kingcent.campus.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询商品分类列表
     *
     * @param pageNum  当前页数
     * @param pageSize 每页查询数量
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<List<CategoryVo>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CategoryEntity> page = new Page<>(pageNum, pageSize);
        page = categoryService.page(page);

        Map<Long, CategoryVo> map = new HashMap<>();
        List<CategoryVo> categoryVos = new ArrayList<>();
        for (CategoryEntity category : page.getRecords()){
            CategoryVo vo = new CategoryVo(category.getId(),category.getName(),category.getThumbnail(),new ArrayList<>());
            map.put(category.getId(), vo);
            Long pid = category.getParentId();
            if(pid != null && map.containsKey(pid)){
                map.get(pid).getChildren().add(vo);
            }else {
                categoryVos.add(vo);
            }
        }

        return Result.success(categoryVos);
    }

    /**
     * 查询所有商品分类列表
     *
     * @return 商品分类列表
     */
    @GetMapping("/all")
    public Result<List<CategoryEntity>> all() {
        LambdaQueryWrapper<CategoryEntity> wrapper = Wrappers.<CategoryEntity>lambdaQuery()
                .eq(CategoryEntity::getIsDeleted, false);
        List<CategoryEntity> list = categoryService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 根据ID查询商品分类详情
     *
     * @param id 商品分类ID
     * @return 商品分类详情
     */
    @GetMapping("/{id}")
    public Result<CategoryEntity> getById(@PathVariable Long id) {
        CategoryEntity category = categoryService.getById(id);
        if (category == null) {
            return Result.fail("未找到该商品分类");
        }
        return Result.success(category);
    }

    /**
     * 添加商品分类
     *
     * @param shopCategory 商品分类信息
     * @return 添加结果
     */
    @PostMapping("")
    public Result<?> add(@RequestBody CategoryEntity shopCategory) {
        boolean saveResult = categoryService.save(shopCategory);
        if (saveResult) {
            return Result.success("添加商品分类成功");
        }
        return Result.fail("添加商品分类失败");
    }

    /**
     * 根据ID更新商品分类信息
     *
     * @param id           商品分类ID
     * @param shopCategory 商品分类信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<?> updateById(@PathVariable Long id, @RequestBody CategoryEntity shopCategory) {
        shopCategory.setId(id);

        boolean updateResult = categoryService.updateById(shopCategory);
        if (updateResult) {
            return Result.success("更新商品分类成功");
        }
        return Result.fail("更新商品分类失败");
    }
}
