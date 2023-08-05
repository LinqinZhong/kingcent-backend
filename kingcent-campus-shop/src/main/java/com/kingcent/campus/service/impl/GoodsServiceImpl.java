package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.*;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.AddressVo;
import com.kingcent.campus.entity.vo.purchase.PurchaseGoodsVo;
import com.kingcent.campus.entity.vo.purchase.PurchaseInfoVo;
import com.kingcent.campus.entity.vo.purchase.PurchaseStoreVo;
import com.kingcent.campus.mapper.GoodsMapper;
import com.kingcent.campus.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, GoodsEntity> implements GoodsService {
}
