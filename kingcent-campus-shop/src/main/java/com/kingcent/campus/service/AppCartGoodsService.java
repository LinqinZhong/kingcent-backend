package com.kingcent.campus.service;

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
public class AppCartGoodsService extends CartGoodsServiceImpl {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private GoodsDiscountService goodsDiscountService;
    @Autowired
    private GoodsSkuService goodsSkuService;

    @Autowired
    private RedisTemplate<String, CartGoodsEntity> redisTemplate;


    /**
     * 生成key
     * @param userId 用户id
     * @return key
     */
    private String key(Long userId){
        return "cart_" + userId;
    }

    /**
     * 生成购物车商品编码
     * @param goodsId 商品id
     * @param strSku sku信息
     * @return cartGoodsCode
     */
    private String createCartGoodeCode(Long goodsId, String strSku){
        return MD5.create().digestHex(goodsId+strSku);
    }


    @Override
    public Result<CartGoodsEntity> updateSku(Long userId, String cartGoodsCode, String specInfo){

        //修改sku，把当前sku的商品删了，创建新sku的商品


        String key = key(userId);

        //redis操作器
        HashOperations<String, String, CartGoodsEntity> ops = redisTemplate.opsForHash();
        CartGoodsEntity cartGoods = ops.get(key, cartGoodsCode);

        //获取购物车记录
        if(cartGoods == null){
            return Result.fail("购物车中不存在该商品",CartGoodsEntity.class);
        }

        //获取新sku
        List<GoodsSkuEntity> sku = goodsSkuService.list(new QueryWrapper<GoodsSkuEntity>()
                .eq("goods_id", cartGoods.getGoodsId())
                .eq("spec_info",specInfo)
                .select("id")
                .last("limit 1"));
        if (sku.size() == 0){
            return Result.fail("该规格的商品已售罄", CartGoodsEntity.class);
        }
        //删除旧sku的商品
        ops.delete(key, cartGoodsCode);
        //生成新的购物车商品编码
        String newCartGoodsCode = createCartGoodeCode(cartGoods.getGoodsId(), specInfo);
        //获取redis中的商品
        CartGoodsEntity newCartGoods = ops.get(key, newCartGoodsCode);
        //为空时创建
        if(newCartGoods == null){
            newCartGoods = new CartGoodsEntity();
            newCartGoods.setSku(specInfo);
            newCartGoods.setChecked(cartGoods.getChecked());
            newCartGoods.setGoodsId(cartGoods.getGoodsId());
            newCartGoods.setCount(0);
        }
        //累计数量
        newCartGoods.setCount(newCartGoods.getCount()+cartGoods.getCount());
        //写入新的数据
        ops.put(key, newCartGoodsCode, newCartGoods);
        return Result.success(cartGoods);
    }

    /**
     * 添加商品到购物车
     * @param vo 添加商品视图
     */
    @Override
    public Result<?> put(Long userId, PutCartGoodsVo vo){


        String strSku = JSON.toJSONString(vo.getSku());
        String key = key(userId);
        String cartGoodsCode = createCartGoodeCode(vo.getGoodsId(),strSku);

        //redis操作器
        HashOperations<String, String, CartGoodsEntity> ops = redisTemplate.opsForHash();

        //获取sku信息
        GoodsSkuEntity sku = goodsSkuService.getOne(
                new QueryWrapper<GoodsSkuEntity>()
                        .eq("spec_info", strSku)
                        .eq("goods_id", vo.getGoodsId())
                        .select("id")
                        .last("limit 1")
        );
        if(sku == null){
            return Result.fail("该商品规格不存在");
        }


        CartGoodsEntity cartGoods = ops.get(key, cartGoodsCode);

        if(cartGoods == null) {
            //购物车不存在该商品，需要获取商品基本信息
            //获取商品信息
            GoodsEntity goods = goodsService.getById(vo.getGoodsId());
            if (goods == null) return Result.fail("商品不存在");
            //新建购物车记录
            cartGoods = new CartGoodsEntity();
            cartGoods.setGoodsId(vo.getGoodsId());
            cartGoods.setSku(strSku);
            cartGoods.setChecked(0);
            cartGoods.setCount(0);
        }

        //修改数量
        cartGoods.setCount(cartGoods.getCount()+vo.getCount());
        //保存数据
        ops.put(key, cartGoodsCode, cartGoods);

        return Result.success("添加成功");
    }

    /**
     * 获取用户的购物车信息
     * @param userId 用户id
     */
    @Override
    public CartVo listByUserId(Long userId){

        String key = key(userId);

        //redis操作器
        HashOperations<String, String, CartGoodsEntity> ops = redisTemplate.opsForHash();

        //获取购物车列表
        List<CartGoodsEntity> cartGoodsEntityList = ops.values(key);
        if(cartGoodsEntityList.size() == 0){
            return new CartVo();
        }


        //提取sku信息
        Set<Long> goodsIds = new HashSet<>();
        Set<String> skuSet = new HashSet<>();
        for (CartGoodsEntity cartGoodsEntity : cartGoodsEntityList) {
            goodsIds.add(cartGoodsEntity.getGoodsId());
            skuSet.add(cartGoodsEntity.getSku());
        }
        //提取商品
        List<GoodsEntity> goodsList = goodsService.list(new QueryWrapper<GoodsEntity>().in("id",goodsIds));
        HashMap<Long, GoodsEntity> goodsMap = new HashMap<>();
        for (GoodsEntity goods : goodsList) {
            goodsMap.put(goods.getId(), goods);
        }

        //提取商铺ID
        Set<Long> shopIds = new HashSet<>();
        for (GoodsEntity goods : goodsList) {
            shopIds.add(goods.getShopId());
        }
        //提取商铺
        List<ShopEntity> shopList = shopService.list(new QueryWrapper<ShopEntity>().in("id",shopIds));
        Map<Long, ShopEntity> shopMap = new HashMap<>();
        for (ShopEntity shopEntity : shopList) {
            shopMap.put(shopEntity.getId(), shopEntity);
        }

        //提取商品规格
        Map<String, GoodsSkuEntity> skuMap = new HashMap<>();
        List<GoodsSkuEntity> skus = goodsSkuService.list(new QueryWrapper<GoodsSkuEntity>().in("spec_info", skuSet));
        for (GoodsSkuEntity goodsSkuEntity : skus) {
            skuMap.put(goodsSkuEntity.getGoodsId()+"-"+goodsSkuEntity.getSpecInfo(), goodsSkuEntity);
        }


        //提取折扣信息
        Map<Long, List<GoodsDiscountEntity>> discountEntityMap = new HashMap<>();
        List<GoodsDiscountEntity> discountEntityList = goodsDiscountService.list(new QueryWrapper<GoodsDiscountEntity>().in("goods_id", goodsIds));
        for (GoodsDiscountEntity goodsDiscountEntity : discountEntityList) {
            List<GoodsDiscountEntity> list;
            if(discountEntityMap.containsKey(goodsDiscountEntity.getGoodsId())){
                list = discountEntityMap.get(goodsDiscountEntity.getGoodsId());
            }else{
                list = new ArrayList<>();
                discountEntityMap.put(goodsDiscountEntity.getGoodsId(), list);
            }
            list.add(goodsDiscountEntity);
        }

        //生成数据
        CartVo cartVo = new CartVo();
        List<CartStoreVo> stores = new ArrayList<>();
        cartVo.setStores(stores);
        Map<Long, List<CartGoodsVo>> storeGoodsListMap = new HashMap<>();

        for (CartGoodsEntity cartGoodsEntity : cartGoodsEntityList) {
            //sku
            GoodsSkuEntity sku = skuMap.get(cartGoodsEntity.getGoodsId()+"-"+cartGoodsEntity.getSku());
            CartGoodsVo cartGoods = new CartGoodsVo();
            //购物车商品信息
            cartGoods.setSku(cartGoodsEntity.getSku());
            cartGoods.setChecked(cartGoodsEntity.getChecked() == 1);
            cartGoods.setCount(cartGoodsEntity.getCount());
            //商品信息
            GoodsEntity goods = goodsMap.get(cartGoodsEntity.getGoodsId());
            cartGoods.setTitle(goods.getName());
            cartGoods.setDefaultThumb(goods.getThumbnail());
            cartGoods.setGoodsId(goods.getId());
            //折扣信息
            if(discountEntityMap.containsKey(goods.getId())){
                List<GoodsDiscountEntity> discounts = discountEntityMap.get(goods.getId());
                List<CartGoodsDiscountVo> discountVos = new ArrayList<>();
                for (GoodsDiscountEntity discount : discounts) {
                    discountVos.add(new CartGoodsDiscountVo(
                            discount.getMoreThan(),
                            discount.getType(),
                            discount.getNum(),
                            discount.getDeadline()
                    ));
                }
                cartGoods.setDiscount(discountVos);
            }
            //规格信息
            if(sku != null){
                cartGoods.setUnitPrice(sku.getPrice());
                cartGoods.setThumb(sku.getImage());
                cartGoods.setSkuInfo(sku.getDescription());
            }


            //添加到对应商铺下
            if(storeGoodsListMap.containsKey(goods.getShopId())){
                storeGoodsListMap.get(goods.getShopId()).add(cartGoods);
            }else{
                List<CartGoodsVo> storeGoods = new ArrayList<>();
                storeGoodsListMap.put(goods.getShopId(), storeGoods);
                CartStoreVo cartStoreVo = new CartStoreVo();
                cartStoreVo.setName(shopMap.get(goods.getShopId()).getName());
                cartStoreVo.setId(goods.getShopId());
                cartStoreVo.setGoodsList(storeGoods);
                stores.add(cartStoreVo);
                storeGoods.add(cartGoods);
            }
        }

        return cartVo;
    }

    @Override
    public Result<?> updateCount(Long userId, String cartGoodsCode, Integer count) {
        if(count <= 0) return Result.fail("count不合法");

        String key = key(userId);
        //redis操作器
        HashOperations<String, String, CartGoodsEntity> ops = redisTemplate.opsForHash();


        //获取购物车记录
        CartGoodsEntity cartGoods = ops.get(key, cartGoodsCode);
        if(cartGoods == null){
            return Result.fail("购物车中不存在该商品",String.class);
        }

        //更新
        cartGoods.setCount(count);
        ops.put(key, cartGoodsCode, cartGoods);

        return Result.success();
    }

    @Override
    public Result<?> updateCheck(Long userId, CartCheckVo check) {
        if(check.getCartGoodsCodes().size() == 0) return Result.success();

        String key = key(userId);
        //redis操作器
        HashOperations<String, String, CartGoodsEntity> ops = redisTemplate.opsForHash();

        //获取购物车记录
        List<CartGoodsEntity> cartGoodsEntities = ops.multiGet(key, check.getCartGoodsCodes());

        Map<String, CartGoodsEntity> cartGoodsEntityMap = new HashMap<>();
        for (CartGoodsEntity cartGoodsEntity : cartGoodsEntities) {
            cartGoodsEntity.setChecked(check.getChecked() ? 1 : 0);
            cartGoodsEntityMap.put(MD5.create().digestHex(cartGoodsEntity.getGoodsId()+cartGoodsEntity.getSku()), cartGoodsEntity);
        }
        if(cartGoodsEntities.size() == 0) return Result.success();
        ops.putAll(key, cartGoodsEntityMap);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long userId, List<String> cartGoodsCodes) {
        String key = key(userId);
        //redis操作器
        HashOperations<String, Long, CartGoodsEntity> ops = redisTemplate.opsForHash();
        for (String code : cartGoodsCodes) {
            ops.delete(key, code);
        }
        return Result.success();
    }
}
