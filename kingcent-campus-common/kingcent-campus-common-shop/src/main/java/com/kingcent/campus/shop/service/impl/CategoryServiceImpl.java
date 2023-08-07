package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.shop.mapper.CategoryMapper;
import com.kingcent.campus.shop.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

}
