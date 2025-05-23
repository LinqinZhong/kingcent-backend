package com.kingcent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.shop.entity.CategoryEntity;
import com.kingcent.common.shop.entity.vo.CategoryVo;
import com.kingcent.common.shop.mapper.CategoryMapper;
import com.kingcent.service.CategoryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/9 10:48
 */
@Service
public class AppShopCategoryService extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Override
    public List<CategoryVo> get(Long parentId, Integer height, Boolean withPrice, Boolean withSales){
        List<CategoryVo> categoryVos = new ArrayList<>();
        if (height == null) height = 1;
        Set<Long> parentIds = new HashSet<>();
        Map<Long, CategoryVo> map = new HashMap<>();
        parentIds.add(parentId);
        for (int i = 0; i < height; i++){
            if(parentIds.size() == 0) break;
            List<CategoryEntity> list = list(new QueryWrapper<CategoryEntity>()
                    .in("parent_id", parentIds)
                    .select("id,name,thumbnail,ref,parent_id")
                    .orderByDesc("weight")
            );
            parentIds.clear();
            for (CategoryEntity category : list){
                CategoryVo vo = new CategoryVo(
                        category.getId(),
                        category.getName(),
                        category.getThumbnail(),
                        null,
                        category.getWeight(),
                        category.getRef(),
                        Boolean.TRUE.equals(withPrice) ? new BigDecimal("9.9") : null,
                        Boolean.TRUE.equals(withSales) ? 100 : null,
                        true
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
        return categoryVos;
    }
}
