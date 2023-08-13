package com.kingcent.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.CategoryVo;
import com.kingcent.campus.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
        List<CategoryVo> categoryVos = new ArrayList<>();
        if (height == null) height = 1;
        Set<Long> parentIds = new HashSet<>();
        Map<Long, CategoryVo> map = new HashMap<>();
        parentIds.add(parentId);
        for (int i = 0; i < height; i++){
            if(parentIds.size() == 0) break;
            List<CategoryEntity> list = categoryService.list(new QueryWrapper<CategoryEntity>()
                    .in("parent_id", parentIds)
                    .select("id,name,thumbnail,ref,parent_id")
            );
            parentIds.clear();
            for (CategoryEntity category : list){
                CategoryVo vo = new CategoryVo(
                        category.getId(),
                        category.getName(),
                        category.getThumbnail(),
                        null,
                        category.getRef(),
                        Boolean.TRUE.equals(withPrice) ? new BigDecimal("9.9") : null,
                        Boolean.TRUE.equals(withSales) ? 100 : null
                );
                parentIds.add(category.getId());
                map.put(category.getId(), vo);
                Long pid = category.getParentId();
                if(pid != null && map.containsKey(pid)){
                    List<CategoryVo> children = map.get(pid).getChildren();
                    if(children == null){
                        children = new ArrayList<>();
                        map.get(pid).setChildren(children);
                    }
                    children.add(vo);
                }else {
                    categoryVos.add(vo);
                }
            }
        }

        return Result.success(categoryVos);
    }

}
