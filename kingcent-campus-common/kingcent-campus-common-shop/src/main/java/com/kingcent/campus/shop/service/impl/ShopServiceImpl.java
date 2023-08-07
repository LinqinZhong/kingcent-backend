package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.ShopEntity;
import com.kingcent.campus.shop.mapper.ShopMapper;
import com.kingcent.campus.shop.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, ShopEntity> implements ShopService {

    @Autowired
    private StringRedisTemplate redisTemplate;


    private static final String KEY_OF_SHOP_NAMES = "shop_names";

    /**
     * 查询商铺名称
     * @param shopIds 商铺id集合
     */
    @Override
    public Map<Long, String> shopNamesMap(Collection<Long> shopIds){
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(KEY_OF_SHOP_NAMES);
        Map<Long, String> names = new HashMap<>();
        List<Long> notFindShopIds = new ArrayList<>();

        for (Long shopId : shopIds) {
            //读取缓存
            String name = ops.get(shopId+"");
            if (name != null){
                //加入结果
                names.put(shopId, name);
            }else{
                //加入搜索集合
                notFindShopIds.add(shopId);
            }
        }

        if(notFindShopIds.size() > 0){
            List<ShopEntity> map = list(new QueryWrapper<ShopEntity>().in("id", notFindShopIds).select("id, name"));
            for (ShopEntity shopEntity : map) {
                //加入缓存
                ops.put(shopEntity.getId() + "", shopEntity.getName());
                //加入结果
                names.put(shopEntity.getId(), shopEntity.getName());
            }
        }
        return names;
    }
}
