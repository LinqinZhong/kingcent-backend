package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.service.WantService;
import com.kingcent.campus.shop.entity.WantEntity;
import com.kingcent.campus.shop.mapper.WantMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/17 6:58
 */
@Service
public class AppWantService extends ServiceImpl<WantMapper,WantEntity> implements WantService {
}
