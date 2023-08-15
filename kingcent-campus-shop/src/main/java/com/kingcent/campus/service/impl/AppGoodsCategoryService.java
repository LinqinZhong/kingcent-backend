package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.GoodsCategoryEntity;
import com.kingcent.campus.shop.mapper.GoodsCategoryMapper;
import com.kingcent.campus.service.GoodsCategoryService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/14 6:44
 */
@Service
public class AppGoodsCategoryService extends ServiceImpl<GoodsCategoryMapper, GoodsCategoryEntity> implements GoodsCategoryService {
}
