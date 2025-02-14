package com.kingcent.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.service.CategoryService;
import com.kingcent.common.result.Result;
import com.kingcent.common.shop.entity.CategoryEntity;
import com.kingcent.common.shop.entity.vo.CategoryVo;
import com.kingcent.common.shop.entity.vo.category.CreateCategoryVo;
import com.kingcent.common.shop.mapper.CategoryMapper;
import com.kingcent.common.shop.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zzy
 */
@Service
@Slf4j
public class AdminCategoryService extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {


    @Override
    public List<CategoryVo> get(Long parentId, Integer height, Boolean withPrice, Boolean withSales){
        List<CategoryVo> categoryVos = new ArrayList<>();
        if(parentId == null) parentId = 0L;
        if (height == null) height = 1;
        Set<Long> parentIds = new HashSet<>();
        Map<Long, CategoryVo> map = new HashMap<>();
        parentIds.add(parentId);

        for (int i = 0; i < height; i++){
            if(parentIds.size() == 0) break;
            List<CategoryEntity> list = list(new QueryWrapper<CategoryEntity>()
                    .in("parent_id", parentIds)
                    .select("id,name,thumbnail,ref,parent_id,weight")
                    .orderByDesc("weight")
            );
            //没有孩子的id
            Set<Long> idNotFindChildren = new HashSet<>();


            if(i == height - 1 && list.size() > 0) {
                for (CategoryEntity category : list) {
                    idNotFindChildren.add(category.getId());
                }
                List<CategoryEntity> list1 = list(new QueryWrapper<CategoryEntity>()
                        .in("parent_id", idNotFindChildren)
                        .isNotNull("id")
                        .select("DISTINCT parent_id AS parent_id")
                );
                for (CategoryEntity category : list1) {
                    idNotFindChildren.remove(category.getParentId());
                }
            }

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
                        idNotFindChildren.contains(category.getId())
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

    @Override
    @Transactional
    public Result<?> save(Long parentId, CreateCategoryVo vo) {
        if (vo.getName() == null || vo.getName().trim().equals("")) return Result.fail("name不能为空");
        Integer weight;
        if(parentId == 0){
            long count = count(new QueryWrapper<CategoryEntity>()
                    .eq("parent_id", 0)
            );
            weight = Math.toIntExact(count);
        } else{
            CategoryEntity parent = getById(parentId);
            if(parent == null) return Result.fail("父分类不存在");
            weight = parent.getChildrenCount();
            update(new UpdateWrapper<CategoryEntity>()
                    .eq("id", parentId)
                    .set("children_count", parent.getChildrenCount() + 1)
            );
        }

        CategoryEntity category = BeanCopyUtils.copyBean(vo, CategoryEntity.class);
        category.setCreateTime(LocalDateTime.now());
        category.setParentId(parentId);
        category.setWeight(weight);
        if(save(category)) return Result.success("创建成功");
        return Result.busy();
    }

    @Override
    public Result<?> delete(Long id) {
        CategoryEntity category = getById(id);
        if(category == null){
            return Result.fail("分类不存在");
        }
        if(removeById(id)) return Result.success("删除成功");
        return Result.busy();
    }

    @Override
    @Transactional
    public Result<?> move(Long id, Integer weight){
        CategoryEntity category = getById(id);
        if(category == null){
            return Result.fail("分类不存在");
        }
        if(category.getWeight().equals(weight)){
            return Result.fail("分类没有移动");
        }else if(update(new UpdateWrapper<CategoryEntity>()
                .eq("parent_id", category.getParentId())
                .le("weight", category.getWeight() > weight ? category.getWeight() : weight)
                .ge("weight", category.getWeight() > weight ? weight : category.getWeight())
                .setSql("weight = IF(" +
                        "id = "+id+","+weight+"," +
                        "weight "+(category.getWeight() > weight  ? '+' : '-')+" 1" +
                        ")"
                )
        )) return Result.success();
        return Result.fail("移动失败");
    }

    @Override
    @Transactional
    public Result<?> resetParent(Long id, Long parentId) {
        CategoryEntity parent = getById(parentId);
        Integer weight;
        if(parent == null){
            long count = count(new QueryWrapper<CategoryEntity>()
                    .eq("parent_id", 0)
            );
            weight = Math.toIntExact(count);
        }else{
            weight = parent.getChildrenCount();
            parent.setChildrenCount(parent.getChildrenCount()+1);
            updateById(parent);
        }
        CategoryEntity category = getById(id);
        if(category == null){
            return Result.fail("分类不存在");
        }
        category.setWeight(weight);
        category.setParentId(parentId);
        updateById(category);
        return Result.success("移动成功");
    }

    @Override
    public List<String> getNames(List<Long> categoryIds) {
        if (categoryIds.size() == 0) return new ArrayList<>();
        List<CategoryEntity> categories = listByIds(categoryIds);
        return categories.stream().map(CategoryEntity::getName).toList();
    }
}