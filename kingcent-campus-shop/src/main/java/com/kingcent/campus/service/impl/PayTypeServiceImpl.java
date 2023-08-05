package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.PayTypeEntity;
import com.kingcent.campus.mapper.PayTypeMapper;
import com.kingcent.campus.service.PayTypeService;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

@Service
public class PayTypeServiceImpl extends ServiceImpl<PayTypeMapper, PayTypeEntity> implements PayTypeService {
}
