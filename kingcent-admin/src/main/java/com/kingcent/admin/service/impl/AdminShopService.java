package com.kingcent.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.service.ShopService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.ShopEntity;
import com.kingcent.common.shop.entity.vo.shop.ShopNameVo;
import com.kingcent.common.shop.mapper.ShopMapper;
import com.kingcent.common.shop.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/14 18:45
 */
@Service
public class AdminShopService extends ServiceImpl<ShopMapper, ShopEntity> implements ShopService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY_OF_SHOP_NAMES = "shop_names";

    @Override
    public Map<Long, String> getShopNames(Collection<Long> shopIds) {
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

    /**
     * 获取店铺名称列表（用于下拉选择店铺）
     * @param keywords 关键词（可以是店铺id，也可以是店铺名称）
     * @param page 页数
     */
    @Override
    public VoList<ShopNameVo> getShopNames(String keywords, Integer page){
        Long id = null;
        //尝试转为数字类型
        try {
            id = Long.valueOf(keywords);
        }catch (Exception ignored){}
        QueryWrapper<ShopEntity> wrapper = new QueryWrapper<>();
        if(id != null) wrapper.eq("id", id);
        if(keywords != null) wrapper.like("name", keywords);
        Page<ShopEntity> pager = new Page<>(page, 10, true);
        Page<ShopEntity> res = page(pager, wrapper);
        List<ShopNameVo> shopNameVoList = BeanCopyUtils.copyBeanList(res.getRecords(), ShopNameVo.class);
        return new VoList<>((int) res.getTotal(), shopNameVoList);
    }

    @Override
    public boolean exists(Long shopId) {
        return count(new QueryWrapper<ShopEntity>().eq("id", shopId)) > 0;
    }

    @Override
    public String getShopName(Long shopId) {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(KEY_OF_SHOP_NAMES);
        String name = ops.get(shopId+"");
        if(name == null){
            ShopEntity shop = getById(shopId);
            name = shop.getName();
            ops.put(shopId+"", name);
        }
        return name;
    }

    @Override
    public Result<VoList<ShopEntity>> list(Integer page, Integer pageSize) {
        Page<ShopEntity> p = new Page<>(page, pageSize,true);
        Page<ShopEntity> res = page(p);
        VoList<ShopEntity> shopEntityVoList = new VoList<>();
        shopEntityVoList.setTotal((int) res.getTotal());
        shopEntityVoList.setRecords(res.getRecords());
        return Result.success(shopEntityVoList);
    }
}
