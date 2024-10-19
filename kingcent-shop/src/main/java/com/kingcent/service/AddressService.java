package com.kingcent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.shop.entity.AddressEntity;
import com.kingcent.common.shop.entity.vo.address.AddressVo;

import java.util.List;

public interface AddressService extends IService<AddressEntity> {
    List<AddressVo> getUserAddress(Long userId);

    boolean setAsDefault(Long userId, Long addressId);
}
