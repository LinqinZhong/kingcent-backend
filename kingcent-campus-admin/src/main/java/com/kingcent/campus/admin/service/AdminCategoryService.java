package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.shop.entity.vo.cart.CategoryVo;
import com.kingcent.campus.shop.mapper.CategoryMapper;
import com.kingcent.campus.shop.service.CategoryService;
import com.kingcent.campus.shop.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzy
 */
@Service
@Slf4j
public class AdminCategoryService extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {


    @Override
    public List<CategoryVo> listAllCategory() {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(CategoryEntity::getId, CategoryEntity::getName);
        List<CategoryEntity> list = list(queryWrapper);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(list, CategoryVo.class);
        return categoryVos;

    }
}
