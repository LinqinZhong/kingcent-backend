package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.GoodsCategoryService;
import com.kingcent.campus.shop.entity.GoodsCategoryEntity;
import com.kingcent.campus.shop.mapper.GoodsCategoryMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/12/3 12:47
 */
@Service
public class AdminGoodsCategoryService extends ServiceImpl<GoodsCategoryMapper, GoodsCategoryEntity> implements GoodsCategoryService {
}
