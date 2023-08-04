package com.kingcent.campus.entity.vo.goods;

import com.kingcent.campus.common.entity.GoodsEntity;
import com.kingcent.campus.common.entity.GoodsSkuEntity;
import com.kingcent.campus.common.entity.GoodsSpecEntity;
import com.kingcent.campus.common.entity.GoodsSpecValueEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GoodsDetailsVo {
    private Long spuId;
    private String title;
    private Long storeId;
    private String images;
    private String desc;
    private Integer soldNum;
    private List<GoodsSpecVo> specList;
    private List<GoodsSkuVo> skuList;

    public void init(GoodsEntity goods, List<GoodsSpecEntity> goodsSpecs, List<GoodsSpecValueEntity> goodsSpecValues, List<GoodsSkuEntity> goodsSkus){
        spuId = goods.getId();
        title = goods.getName();

        storeId = goods.getShopId();
        images = goods.getImages();
        desc = goods.getDescription();
        soldNum = 0;

        //
        Map<Long, List<GoodsSpecValueVo>> specValMap = new HashMap<>();
        for (GoodsSpecValueEntity goodsSpecValue : goodsSpecValues) {
            GoodsSpecValueVo vo = new GoodsSpecValueVo();

            vo.setSpecValueId(goodsSpecValue.getId());
            vo.setSpecId(goodsSpecValue.getSpecId());
            vo.setImage(goodsSpecValue.getImage());
            vo.setSpecValue(goodsSpecValue.getVal());

            if(specValMap.containsKey(goodsSpecValue.getSpecId())){
                specValMap.get(goodsSpecValue.getSpecId()).add(vo);
            }else{
                List<GoodsSpecValueVo> specValVo = new ArrayList<>();
                specValVo.add(vo);
                specValMap.put(goodsSpecValue.getSpecId(),specValVo);
            }

        }
        //
        specList = new ArrayList<>();
        for (GoodsSpecEntity goodsSpec : goodsSpecs) {
            GoodsSpecVo goodsSpecVo = new GoodsSpecVo();
            goodsSpecVo.setTitle(goodsSpec.getTitle());
            goodsSpecVo.setSpecId(goodsSpec.getId());
            goodsSpecVo.setSpecValueList(specValMap.get(goodsSpec.getId()));
            specList.add(goodsSpecVo);
        }

        //
        skuList = new ArrayList<>();
        for (GoodsSkuEntity sku : goodsSkus) {
            GoodsSkuVo skuVo = new GoodsSkuVo();
            skuVo.setSpecInfo(sku.getSpecInfo());
            skuVo.setSkuImage(sku.getImage());
            skuVo.setSkuId(sku.getId());
            skuVo.setLimitMaxCount(sku.getLimitMaxCount());
            skuVo.setLimitMinCount(sku.getLimitMinCount());
            skuVo.setPrice(sku.getPrice());
            skuVo.setOriginalPrice(sku.getOriginalPrice());
            soldNum += sku.getSoldQuantity();
            skuVo.setStock(sku.getSafeStockQuantity());
            skuList.add(skuVo);
        }
    }
}
