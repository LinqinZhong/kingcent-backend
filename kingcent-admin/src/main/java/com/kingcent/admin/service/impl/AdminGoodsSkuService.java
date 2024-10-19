package com.kingcent.admin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.entity.vo.EditSkuVo;
import com.kingcent.admin.service.GoodsService;
import com.kingcent.admin.service.GoodsSkuService;
import com.kingcent.admin.service.GoodsSpecValueService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.GoodsEntity;
import com.kingcent.common.shop.entity.GoodsSkuEntity;
import com.kingcent.common.shop.entity.GoodsSpecValueEntity;
import com.kingcent.common.shop.mapper.GoodsSkuMapper;
import com.kingcent.common.shop.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/27 10:08
 */
@Service
public class AdminGoodsSkuService extends ServiceImpl<GoodsSkuMapper, GoodsSkuEntity> implements GoodsSkuService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsSpecValueService specValueService;

    @Override
    public Result<VoList<GoodsSkuEntity>> list(Long shopId, Long goodsId, Integer page, Integer pageSize) {

        Page<GoodsSkuEntity> p = new Page<>(page,pageSize,true);
        Page<GoodsSkuEntity> res = page(p, new QueryWrapper<GoodsSkuEntity>()
                .eq("goods_id", goodsId)
        );
        VoList<GoodsSkuEntity> voList = new VoList<>();
        voList.setRecords(res.getRecords());
        voList.setTotal((int) res.getTotal());
        return Result.success(voList);
    }

    @Override
    public Result<?> delete(Long shopId, Long goodsId, Long skuId){
        if(!goodsService.exist(shopId, goodsId)){
            return Result.fail("商品不存在");
        }
        if(remove(new QueryWrapper<GoodsSkuEntity>()
                .eq("goods_id", goodsId)
                .eq("id",skuId)
        )){
            recalculate(goodsId);
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    @Override
    @Transactional
    public Result<?> create(Long shopId, Long goodsId, EditSkuVo vo) {
        if(vo.getSpecInfo() == null){
            return Result.fail("请选择完整的规格");
        }
        if(vo.getStockQuantity() == null){
            return Result.fail("库存不能为空");
        }
        if(vo.getStockQuantity() < 0){
            return Result.fail("库存不能为负数");
        }
        if(vo.getImage() == null){
            return Result.fail("请选择图片");
        }
        if(vo.getDescription() == null){
            return Result.fail("描述不能为空");
        }
        if(vo.getCost() == null || vo.getOriginalPrice() == null || vo.getPrice() == null){
            return Result.fail("价格不能为空");
        }
        if(vo.getCost().doubleValue() < 0|| vo.getOriginalPrice().doubleValue() < 0 || vo.getPrice().doubleValue() < 0){
            return Result.fail("价格不能为负数");
        }

        if(vo.getLimitMinCount() != null){
            if(vo.getLimitMinCount() == 0){
                vo.setLimitMinCount(null);
            }else if(vo.getLimitMinCount() < 0) {
                return Result.fail("起购数量只能为正整数");
            }
        }
        if(vo.getLimitMaxCount() != null){
            if(vo.getLimitMaxCount() == 0){
                vo.setLimitMaxCount(null);
            }else if(vo.getLimitMaxCount() < 0){
                return Result.fail("限购数量只能为正整数");
            }
        }
        if (vo.getLimitMinCount() != null && vo.getLimitMaxCount() != null && vo.getLimitMaxCount() < vo.getLimitMinCount()){
            return Result.fail("起购数量不能大于限购数量");
        }
        if(vo.getPrice().doubleValue() > vo.getOriginalPrice().doubleValue()){
            return Result.fail("售价不能大于原价");
        }
        if(vo.getCost().doubleValue() > vo.getPrice().doubleValue()){
            return Result.fail("售价不能小于成本");
        }

        if(!goodsService.exist(shopId, goodsId)){
            return Result.fail("商品不存在");
        }
        try {
            QueryWrapper<GoodsSpecValueEntity> wrapper = new QueryWrapper<>();
            List<JSONArray> spec = JSONObject.parseArray(vo.getSpecInfo(), JSONArray.class);
            wrapper.eq("goods_id", goodsId);
            wrapper.and(w -> {
                for (JSONArray item : spec) {
                    w.or(w0 -> {
                        w0.eq("spec_id", item.get(0));
                        w0.eq("id", item.get(1));
                    });
                }
            });
            if (specValueService.count(wrapper) != spec.size()) {
                return Result.fail("规格不存在");
            }
        } catch (Exception e) {
            return Result.fail("规格格式错误");
        }
        //规格是否存在
        if(hasSpecInfo(goodsId, vo.getSpecInfo(), null)){
            return Result.fail("该规格的商品已经存在");
        }
        GoodsSkuEntity sku = BeanCopyUtils.copyBean(vo,GoodsSkuEntity.class);
        sku.setSafeStockQuantity(sku.getStockQuantity());
        sku.setSoldQuantity(0);
        sku.setGoodsId(goodsId);
        sku.setCreateTime(LocalDateTime.now());
        if(save(sku)){
            recalculate(goodsId);
            return Result.success("创建成功");
        }
        return Result.fail("创建失败");
    }
    @Override
    @Transactional
    public Result<?> update(Long shopId, Long goodsId, Long skuId, EditSkuVo vo) {
        if(!goodsService.exist(shopId, goodsId)){
            return Result.fail("商品不存在");
        }

        if(vo.getCost().doubleValue() < 0|| vo.getOriginalPrice().doubleValue() < 0 || vo.getPrice().doubleValue() < 0){
            return Result.fail("价格不能为负数");
        }

        if(vo.getLimitMinCount() != null && vo.getLimitMinCount() <= 0){
            return Result.fail("起购数量只能为正整数");
        }
        if(vo.getLimitMaxCount() != null && vo.getLimitMaxCount() <= 0){
            return Result.fail("限购数量只能为正整数");
        }
        if (vo.getLimitMinCount() != null && vo.getLimitMaxCount() != null && vo.getLimitMaxCount() < vo.getLimitMinCount()){
            return Result.fail("起购数量不能大于限购数量");
        }
        if(vo.getPrice().doubleValue() > vo.getOriginalPrice().doubleValue()){
            return Result.fail("售价不能大于原价");
        }
        if(vo.getCost().doubleValue() > vo.getPrice().doubleValue()){
            return Result.fail("售价不能小于成本");
        }

        GoodsEntity goods = goodsService.getOne(
                new QueryWrapper<GoodsEntity>()
                        .eq("id", goodsId)
                        .eq("shop_id", shopId)
                        .select("is_sale")
        );
        if(goods == null){
            return Result.fail("商品不存在");
        }
        if(goods.getIsSale() == 1){
            return Result.fail("商品上架中，请勿修改");
        }

        if(vo.getSpecInfo()!=null) {
            try {
                QueryWrapper<GoodsSpecValueEntity> wrapper = new QueryWrapper<>();
                List<JSONArray> spec = JSONObject.parseArray(vo.getSpecInfo(), JSONArray.class);
                wrapper.eq("goods_id", goodsId);
                wrapper.and(w -> {
                    for (JSONArray item : spec) {
                        w.or(w0 -> {
                            w0.eq("spec_id", item.get(0));
                            w0.eq("id", item.get(1));
                        });
                    }
                });
                if (specValueService.count(wrapper) != spec.size()) {
                    return Result.fail("规格不存在");
                }
            } catch (Exception e) {
                return Result.fail("规格格式错误");
            }
            //规格是否存在
            if(hasSpecInfo(goodsId, vo.getSpecInfo(), skuId)){
                return Result.fail("该规格的商品已经存在");
            }
        }
        if(update(
                new UpdateWrapper<GoodsSkuEntity>()
                        .eq("goods_id", goodsId)
                        .eq("id", skuId)
                        .set("cost", vo.getCost())
                        .set("price", vo.getPrice())
                        .setSql("safe_stock_quantity =  safe_stock_quantity - stock_quantity + "+vo.getStockQuantity())
                        .set("stock_quantity", vo.getStockQuantity())
                        .set("description", vo.getDescription())
                        .set("image", vo.getImage())
                        .set("original_price",vo.getOriginalPrice())
                        .set("limit_min_count", vo.getLimitMinCount())
                        .set("limit_max_count", vo.getLimitMaxCount())
                        .set("spec_info", vo.getSpecInfo())
        )){
            recalculate(goodsId);
            return Result.success("修改成功");
        }
        return Result.fail("修改失败");
    }

    /**
     * 重新统计商品的价格
     */
    private void recalculate(Long goodsId){
        GoodsSkuEntity sku = getOne(new QueryWrapper<GoodsSkuEntity>()
                .eq("goods_id", goodsId)
                .select("MIN(price) AS price, MAX(original_price) AS original_price")
        );
        //sku为空，下架商品
        if(sku == null) {
            goodsService.update(new UpdateWrapper<GoodsEntity>()
                    .eq("id", goodsId)
                    .set("price", 0)
                    .set("original_price", 0)
                    .set("is_sale", 0)
            );
            return;
        };
        goodsService.update(
                new UpdateWrapper<GoodsEntity>()
                        .eq("id",goodsId)
                        .set("price", sku.getPrice())
                        .set("original_price", sku.getOriginalPrice())
        );
    }

    private boolean hasSpecInfo(Long goodsId, String specInfo, Long ignoreSkuId){
        QueryWrapper<GoodsSkuEntity> w = new QueryWrapper<GoodsSkuEntity>()
                .eq("goods_id", goodsId)
                .eq("spec_info", specInfo)
                .last("limit 1");
        if(ignoreSkuId != null) w.ne("id", ignoreSkuId);
        return count(w) > 0;
    }
}
