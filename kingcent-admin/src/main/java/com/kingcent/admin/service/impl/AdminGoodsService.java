package com.kingcent.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.service.CategoryService;
import com.kingcent.admin.service.GoodsCategoryService;
import com.kingcent.admin.service.GoodsService;
import com.kingcent.admin.service.ShopService;
import com.kingcent.common.result.Result;
import com.kingcent.common.shop.entity.GoodsCategoryEntity;
import com.kingcent.common.shop.entity.GoodsEntity;
import com.kingcent.common.shop.entity.vo.goods.GoodsInfoVo;
import com.kingcent.common.shop.mapper.GoodsMapper;
import com.kingcent.common.shop.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zzy
 */
@Service
@Slf4j
public class AdminGoodsService extends ServiceImpl<GoodsMapper, GoodsEntity> implements GoodsService {

    @Autowired
    private ShopService shopService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public boolean exist(Long shopId, Long goodsId) {
        return baseMapper.exists(
                new QueryWrapper<GoodsEntity>()
                        .eq("shop_id", shopId)
                        .eq("id",goodsId)
        );
    }

    @Override
    public Result<GoodsInfoVo> info(Long shopId, Long goodsId) {
        GoodsEntity goods = getOne(new QueryWrapper<GoodsEntity>()
                .eq("id", goodsId)
                .eq("shop_id", shopId));
        if(goods == null) return Result.fail("商品不存在");
        GoodsInfoVo vo = new GoodsInfoVo();
        vo.setId(goods.getId());
        vo.setImages(goods.getImages());
        vo.setDescription(goods.getDescription());
        vo.setName(goods.getName());
        vo.setThumbnail(goods.getThumbnail());
        vo.setShopName(shopService.getShopName(goods.getShopId()));
        vo.setShopId(goods.getShopId());
        //获取商品分类
        List<Long> categoryIds = new ArrayList<>();
        List<GoodsCategoryEntity> categories = goodsCategoryService.list(new QueryWrapper<GoodsCategoryEntity>()
                .eq("goods_id", goodsId)
        );
        for (GoodsCategoryEntity category : categories) {
            categoryIds.add(category.getCategoryId());
        }
        //查询商品分类名称
        List<String> categoryNames = categoryService.getNames(categoryIds);
        vo.setCategoryIds(String.join(",", categoryIds.stream().map(id -> id+"").toList()));
        vo.setCategoryNames(String.join(",",categoryNames));


        return Result.success(vo);
    }

    @Override
    @Transactional
    public Result<?> save(Long shopId, GoodsInfoVo vo) {
        if(!shopService.exists(shopId)) return Result.fail("店铺不存在");
        if(vo.getName() == null) return Result.fail("商品名称不能为空");
        if(vo.getThumbnail() == null) return Result.fail("商品缩略图不能为空");
        GoodsEntity goods = BeanCopyUtils.copyBean(vo, GoodsEntity.class);
        goods.setCreateTime(LocalDateTime.now());
        goods.setPrice(BigDecimal.valueOf(0));
        goods.setName(vo.getName());
        goods.setImages(String.join(",",vo.getImages()));
        goods.setOriginalPrice(BigDecimal.valueOf(0));
        goods.setDescription(String.join(",",vo.getDescription()));
        goods.setShopId(shopId);
        goods.setIsSale(0);
        save(goods);
        //绑定分类
        if(vo.getCategoryIds() != null){
            List<GoodsCategoryEntity> categoryEntityList = new ArrayList<>();
            for (String s : vo.getCategoryIds().split(",")) {
                GoodsCategoryEntity category = new GoodsCategoryEntity();
                category.setCategoryId(Long.parseLong(s));
                category.setGoodsId(goods.getId());
                categoryEntityList.add(category);
            }
            goodsCategoryService.saveBatch(categoryEntityList);
        }
        return Result.success("创建成功");
    }

    @Override
    public Result<?> update(Long goodsId, GoodsInfoVo vo) {
        GoodsEntity goods = BeanCopyUtils.copyBean(vo, GoodsEntity.class);
        goods.setId(goodsId);
        goods.setImages(String.join(",",vo.getImages()));
        goods.setDescription(String.join(",",vo.getDescription()));

        if(updateById(goods)) {
            if(vo.getCategoryIds() != null){
                Set<Long> newCategoryIds = new HashSet<>();
                for (String s : vo.getCategoryIds().split(",")) {
                    newCategoryIds.add(Long.parseLong(s));
                }
                //获取商品分类
                List<Long> categoryIdsToDel = new ArrayList<>();
                List<GoodsCategoryEntity> categories = goodsCategoryService.list(new QueryWrapper<GoodsCategoryEntity>()
                        .eq("goods_id", goodsId)
                );
                for (GoodsCategoryEntity category : categories) {
                    if(!newCategoryIds.contains(category.getCategoryId())){
                        categoryIdsToDel.add(category.getCategoryId());
                    }else{
                        newCategoryIds.remove(category.getCategoryId());
                    }
                }
                //删除没有的分类
                if(categoryIdsToDel.size() > 0) {
                    goodsCategoryService.remove(new QueryWrapper<GoodsCategoryEntity>()
                            .eq("goods_id", goodsId)
                            .in("category_id", categoryIdsToDel)
                    );
                }
                //新增绑定分类
                if(newCategoryIds.size() > 0) {
                    List<GoodsCategoryEntity> categoryEntityList = new ArrayList<>();
                    for (Long s : newCategoryIds) {
                        GoodsCategoryEntity category = new GoodsCategoryEntity();
                        category.setCategoryId(s);
                        category.setGoodsId(goods.getId());
                        categoryEntityList.add(category);
                    }
                    goodsCategoryService.saveBatch(categoryEntityList);
                }
            }
            return Result.success("修改成功");
        }
        return Result.fail("商品不存在");
    }
}
