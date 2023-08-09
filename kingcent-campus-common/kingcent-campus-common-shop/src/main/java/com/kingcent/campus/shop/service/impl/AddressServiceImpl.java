package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.AddressEntity;
import com.kingcent.campus.shop.entity.vo.AddressVo;
import com.kingcent.campus.shop.mapper.AddressMapper;
import com.kingcent.campus.shop.service.AddressService;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressEntity> implements AddressService {

    @Override
    public List<AddressVo> getUserAddress(Long userId) {
        return null;
    }
}
