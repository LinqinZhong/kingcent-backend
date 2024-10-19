package com.kingcent.admin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.entity.vo.EditSpecVo;
import com.kingcent.admin.service.GoodsSkuService;
import com.kingcent.admin.service.GoodsSpecService;
import com.kingcent.admin.service.GoodsSpecValueService;
import com.kingcent.admin.service.GoodsService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.shop.entity.GoodsSkuEntity;
import com.kingcent.common.shop.entity.GoodsSpecEntity;
import com.kingcent.common.shop.entity.GoodsSpecValueEntity;
import com.kingcent.common.shop.entity.vo.goods.GoodsSpecValueVo;
import com.kingcent.common.shop.entity.vo.goods.GoodsSpecVo;
import com.kingcent.common.shop.mapper.GoodsSpecMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/27 9:06
 */
@Service
public class AdminGoodsSpecService extends ServiceImpl<GoodsSpecMapper, GoodsSpecEntity> implements GoodsSpecService {

    @Autowired
    private GoodsSpecValueService specValueService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsSkuService skuService;

    @Override
    public Result<List<GoodsSpecVo>> getSpecList(Long shopId, Long goodsId) {
        List<GoodsSpecEntity> specs = list(
                new QueryWrapper<GoodsSpecEntity>()
                        .eq("goods_id", goodsId)
        );
        List<GoodsSpecValueEntity> specValues = specValueService.list(
                new QueryWrapper<GoodsSpecValueEntity>()
                        .eq("goods_id", goodsId)
        );
        Map<Long,List<GoodsSpecValueVo>> valueListMap = new HashMap<>();
        List<GoodsSpecVo> res = new ArrayList<>();
        for (GoodsSpecEntity spec : specs) {
            GoodsSpecVo vo = new GoodsSpecVo();
            vo.setSpecId(spec.getId());
            vo.setTitle(spec.getTitle());
            List<GoodsSpecValueVo> valueVos = new ArrayList<>();
            vo.setSpecValueList(valueVos);
            valueListMap.put(spec.getId(), valueVos);
            res.add(vo);
        }
        for (GoodsSpecValueEntity specValue : specValues) {
            if (valueListMap.containsKey(specValue.getSpecId())) {
                GoodsSpecValueVo vo = new GoodsSpecValueVo();
                vo.setSpecId(specValue.getSpecId());
                vo.setSpecValueId(specValue.getId());
                vo.setImage(specValue.getImage());
                vo.setSpecTitle(specValue.getVal());
                valueListMap.get(specValue.getSpecId()).add(vo);
            }
        }
        return Result.success(res);
    }

    @Override
    public Result<?> update(Long shopId, Long goodsId, Long specId, EditSpecVo vo) {
        if(update(new UpdateWrapper<GoodsSpecEntity>()
                .eq("id",specId)
                .eq("goods_id", goodsId)
                .set("title",vo.getTitle())
        )) return Result.success("修改成功");
        return Result.fail("修改失败");
    }

    @Override
    @Transactional
    public Result<?> create(Long shopId, Long goodsId, EditSpecVo vo) {
        if (!goodsService.exist(shopId, goodsId)) {
            return Result.fail("商品不存在");
        }
        GoodsSpecEntity entity = new GoodsSpecEntity();
        entity.setGoodsId(goodsId);
        entity.setTitle(vo.getTitle());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        if(save(entity)){
            //为规格创建默认选项
            GoodsSpecValueEntity specValue = new GoodsSpecValueEntity();
            specValue.setCreateTime(LocalDateTime.now());
            specValue.setUpdateTime(LocalDateTime.now());
            specValue.setSpecId(entity.getId());
            specValue.setGoodsId(goodsId);
            specValue.setImage("");
            specValue.setVal("默认选项（请修改）");
            if(specValueService.save(specValue))
                return Result.success("创建成功");
        }
        return Result.fail("创建失败");
    }

    @Override
    public Result<?> delete(Long shopId, Long goodsId, Long specId) {
        if(!goodsService.exist(shopId, goodsId)){
            return Result.fail("商品不存在");
        }

        List<GoodsSkuEntity> skus = skuService.list(new QueryWrapper<GoodsSkuEntity>()
                .eq("goods_id", goodsId)
                .select("spec_info")
        );
        for (GoodsSkuEntity sku : skus) {
            for (JSONArray array : JSONObject.parseArray(sku.getSpecInfo(), JSONArray.class)) {
                if(array.getLong(0).equals(specId)){
                    return Result.fail("删除失败，SKU列表中有该规格的商品");
                }
            }
        }

        if(remove(new QueryWrapper<GoodsSpecEntity>()
                .eq("goods_id", goodsId)
                .eq("id", specId))
        ) return Result.success("删除成功");

        return Result.fail("商品规格不存在");
    }

    @Override
    public boolean batchDelete(Long shopId, Long goodsId, Collection<Long> specIds) {
        //过滤存在SKU的规格
        List<GoodsSkuEntity> skus = skuService.list(new QueryWrapper<GoodsSkuEntity>()
                .eq("goods_id", goodsId)
                .select("spec_info")
        );
        for (GoodsSkuEntity sku : skus) {
            for (JSONArray array : JSONObject.parseArray(sku.getSpecInfo(), JSONArray.class)) {
                specIds.removeIf(specId -> array.getLong(0).equals(specId));
            }
        }
        if(specIds.size() == 0) return true;
        return remove(new QueryWrapper<GoodsSpecEntity>()
                .eq("goods_id", goodsId)
                .in("id", specIds)
        );
    }
}
