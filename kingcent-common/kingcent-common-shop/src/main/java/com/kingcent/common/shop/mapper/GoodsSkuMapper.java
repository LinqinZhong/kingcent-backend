package com.kingcent.common.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.common.shop.entity.GoodsSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsSkuMapper extends BaseMapper<GoodsSkuEntity> {

    /**
     * 更新库存
     */
    @Update("UPDATE kc_shop_goods_sku " +
            "SET stock_quantity = stock_quantity + #{delta} " +
            "WHERE id = #{id}"
    )
    boolean updateStockQuantity(long id, int delta);

    /**
     * 更新安全库存
     */
    @Update("UPDATE kc_shop_goods_sku " +
            "SET safe_stock_quantity = safe_stock_quantity + #{delta}," +
            "sold_quantity = sold_quantity - #{delta} " +
            "WHERE id = #{id}\n"+
            "AND safe_stock_quantity + #{delta} >= 0"
    )
    boolean changeSafeStockQuantityById(long id, int delta);

    /**
     * 更新安全库存
     */
    @Update("UPDATE kc_shop_goods_sku " +
            "SET safe_stock_quantity = safe_stock_quantity + #{delta}," +
            "sold_quantity = sold_quantity - #{delta} " +
            "WHERE spec_info = #{specInfo} " +
            "AND goods_id = #{goodsId} " +
            "AND safe_stock_quantity + #{delta} >= 0 "
    )
    boolean changeSafeStockQuantityBySpecInfo(long goodsId, String specInfo, int delta);
}
