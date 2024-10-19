package com.kingcent.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.service.GoodsCategoryService;
import com.kingcent.common.shop.entity.GoodsCategoryEntity;
import com.kingcent.common.shop.mapper.GoodsCategoryMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/12/3 12:47
 */
@Service
public class AdminGoodsCategoryService extends ServiceImpl<GoodsCategoryMapper, GoodsCategoryEntity> implements GoodsCategoryService {
}
