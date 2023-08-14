package com.kingcent.campus.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.campus.shop.entity.GoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GoodsMapper extends BaseMapper<GoodsEntity> {
    @Select("<script>" +
            "   SELECT " +
            "       spu.id AS id," +
            "       spu.name AS name," +
            "       spu.shop_id AS shop_id," +
            "       spu.price AS price," +
            "       spu.original_price AS original_price," +
            "       spu.thumbnail AS thumbnail," +
            "       SUM(sku.sold_quantity) AS sales" +
            "   FROM kc_shop_goods spu" +
            "   LEFT OUTER JOIN kc_shop_goods_sku sku " +
            "   ON spu.id = sku.goods_id" +
            "   WHERE spu.is_sale = 1" +
            "   AND spu.is_deleted = 0" +
            "   AND sku.is_deleted = 0" +
            "   <if test='shopIds != null'>"+
            "       AND spu.shop_id IN" +
            "           <foreach " +
            "               collection='shopIds'" +
            "               item='id'" +
            "               open='('" +
            "               close=')'" +
            "               separator=','" +
            "           >" +
            "               #{id}"+
            "           </foreach>" +
            "   </if>"+
            "   <if test='goodsIds != null'>"+
            "       AND spu.id IN" +
            "           <foreach " +
            "               collection='goodsIds'" +
            "               item='id'" +
            "               open='('" +
            "               close=')'" +
            "               separator=','" +
            "           >" +
            "               #{id}"+
            "           </foreach>" +
            "   </if>"+
            "   <if test='keywords != null'>" +
            "       AND spu.name LIKE #{keywords}" +
            "   </if>"+
            "   GROUP BY sku.goods_id" +
            "   ORDER BY sales DESC" +
            "   LIMIT #{offset},#{pageSize} "+
            "</script>"
    )
    List<GoodsEntity> selectGoodsBySalesDesc(Collection<Long> shopIds,Collection<Long> goodsIds,String keywords, Integer offset, Integer pageSize);

}
