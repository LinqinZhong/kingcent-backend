package com.kingcent.admin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.entity.vo.EditSpecValVo;
import com.kingcent.admin.service.GoodsService;
import com.kingcent.admin.service.GoodsSkuService;
import com.kingcent.admin.service.GoodsSpecService;
import com.kingcent.admin.service.GoodsSpecValueService;
import com.kingcent.common.result.Result;
import com.kingcent.common.shop.entity.GoodsSkuEntity;
import com.kingcent.common.shop.entity.GoodsSpecEntity;
import com.kingcent.common.shop.entity.GoodsSpecValueEntity;
import com.kingcent.common.shop.mapper.GoodsSpecValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/27 9:23
 */
@Service
public class AdminGoodsSpecValueService extends ServiceImpl<GoodsSpecValueMapper, GoodsSpecValueEntity> implements GoodsSpecValueService {

    @Autowired
    @Lazy
    private GoodsService goodsService;

    @Autowired
    @Lazy
    private GoodsSpecService specService;

    @Autowired
    @Lazy
    private GoodsSkuService skuService;

    @Override
    public Result<?> update(Long shopId, Long goodsId, Long specId, Long specValId, EditSpecValVo vo) {
        if(update(
                new UpdateWrapper<GoodsSpecValueEntity>()
                        .eq("id", specValId)
                        .eq("spec_id", specId)
                        .eq("goods_id", goodsId)
                        .set("val", vo.getVal())
                        .set("image", vo.getImage())
        )) return Result.success("修改成功");
        return Result.fail("修改失败");
    }

    @Override
    public Result<?> create(Long shopId, Long goodsId, Long specId, EditSpecValVo vo) {
        if(!goodsService.exist(shopId, goodsId) || !specService.getBaseMapper().exists(
                new QueryWrapper<GoodsSpecEntity>()
                        .eq("goods_id", goodsId)
                        .eq("id",specId)
        )){
            return Result.fail("商品规格标题不存在");
        }
        GoodsSpecValueEntity specValue = new GoodsSpecValueEntity();
        specValue.setVal(vo.getVal());
        specValue.setImage(vo.getImage());
        specValue.setSpecId(specId);
        specValue.setGoodsId(goodsId);
        specValue.setCreateTime(LocalDateTime.now());
        specValue.setUpdateTime(LocalDateTime.now());
        if(save(specValue)){
            return Result.success("创建成功");
        }
        return Result.fail("创建失败");
    }

    @Override
    public Result<?> delete(Long shopId, Long goodsId, Long specId, Long specValId) {
        if(!goodsService.exist(shopId, goodsId)){
            return Result.fail("商品不存在");
        }

        List<GoodsSkuEntity> skus = skuService.list(new QueryWrapper<GoodsSkuEntity>()
                .eq("goods_id", goodsId)
                .select("spec_info")
        );
        for (GoodsSkuEntity sku : skus) {
            for (JSONArray array : JSONObject.parseArray(sku.getSpecInfo(), JSONArray.class)) {
                if(array.getLong(1).equals(specValId)){
                    return Result.fail("删除失败，SKU列表中有该规格的商品");
                }
            }
        }

        if(count(
                new QueryWrapper<GoodsSpecValueEntity>()
                        .eq("spec_id",specId)
        ) == 1) return Result.fail("该规格至少保留一个选项");

        if(remove(new QueryWrapper<GoodsSpecValueEntity>()
                .eq("goods_id", goodsId)
                .eq("id", specValId))
        ) return Result.success("删除成功");

        return Result.fail("商品规格不存在");
    }

    @Override
    public boolean batchDelete(Long shopId, Long goodsId, Collection<Long> specValIds) {
        //过滤存在SKU的规格选项
        List<GoodsSkuEntity> skus = skuService.list(new QueryWrapper<GoodsSkuEntity>()
                .eq("goods_id", goodsId)
                .select("spec_info")
        );
        for (GoodsSkuEntity sku : skus) {
            for (JSONArray array : JSONObject.parseArray(sku.getSpecInfo(), JSONArray.class)) {
                specValIds.removeIf(specValId -> array.getLong(1).equals(specValId));
            }
        }
        if(specValIds.size() == 0) return true;
        return remove(
                new QueryWrapper<GoodsSpecValueEntity>()
                        .eq("goods_id", goodsId)
                        .in("id", specValIds)
        );
    }
}
