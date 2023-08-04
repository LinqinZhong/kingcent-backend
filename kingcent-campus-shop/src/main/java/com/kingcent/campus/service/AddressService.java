package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.AddressEntity;
import com.kingcent.campus.entity.vo.AddressVo;

import java.util.List;

public interface AddressService extends IService<AddressEntity> {
    List<AddressVo> getUserAddress(Long userId);
}
