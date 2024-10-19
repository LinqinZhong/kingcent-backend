package com.kingcent.common.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.common.shop.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}
