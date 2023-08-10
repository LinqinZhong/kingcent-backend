package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.shop.mapper.CategoryMapper;
import com.kingcent.campus.shop.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/9 10:48
 */
@Service
public class AppShopCategoryService extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {
}
