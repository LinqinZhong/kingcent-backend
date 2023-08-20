package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.AddressEntity;
import com.kingcent.campus.shop.entity.vo.address.AddressVo;

import java.util.List;

public interface AddressService extends IService<AddressEntity> {
    List<AddressVo> getUserAddress(Long userId);

    boolean setAsDefault(Long userId, Long addressId);
}
