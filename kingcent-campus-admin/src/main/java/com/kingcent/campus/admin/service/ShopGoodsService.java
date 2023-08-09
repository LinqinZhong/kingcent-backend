package com.kingcent.campus.admin.service;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.*;
import com.kingcent.campus.shop.entity.vo.cart.*;
import com.kingcent.campus.shop.entity.vo.purchase.PutCartGoodsVo;
import com.kingcent.campus.shop.service.GoodsDiscountService;
import com.kingcent.campus.shop.service.GoodsService;
import com.kingcent.campus.shop.service.GoodsSkuService;
import com.kingcent.campus.shop.service.ShopService;
import com.kingcent.campus.shop.service.impl.CartGoodsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/9 10:46
 */
@Service
public class ShopGoodsService extends CartGoodsServiceImpl {
}
