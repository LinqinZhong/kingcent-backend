package com.kingcent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.shop.entity.GoodsCategoryEntity;
import com.kingcent.common.shop.mapper.GoodsCategoryMapper;
import com.kingcent.service.GoodsCategoryService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/14 6:44
 */
@Service
public class AppGoodsCategoryService extends ServiceImpl<GoodsCategoryMapper, GoodsCategoryEntity> implements GoodsCategoryService {
}
