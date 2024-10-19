package com.kingcent.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.dto.EditGoodsDiscountDto;
import com.kingcent.admin.service.GoodsDiscountService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.GoodsDiscountEntity;
import com.kingcent.common.shop.mapper.GoodsDiscountMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2023/11/30 0:17
 */
@Service
public class AdminGoodsDiscountService extends ServiceImpl<GoodsDiscountMapper, GoodsDiscountEntity> implements GoodsDiscountService {
    @Override
    public Result<VoList<GoodsDiscountEntity>> list(Long shopId, Long goodsId, Integer page, Integer pageSize) {
        Page<GoodsDiscountEntity> p = new Page<>(page, pageSize, true);
        Page<GoodsDiscountEntity> res = page(p, new QueryWrapper<GoodsDiscountEntity>()
                .eq("shop_id", shopId)
                .eq("goods_id", goodsId)
        );
        return Result.success(new VoList<>((int) res.getTotal(), res.getRecords()));
    }

    @Override
    public Result<?> save(Long shopId, Long goodsId, EditGoodsDiscountDto dto) {
        GoodsDiscountEntity entity = new GoodsDiscountEntity();
        entity.setGoodsId(goodsId);
        entity.setShopId(shopId);
        entity.setDeadline(dto.getDeadline());
        entity.setMoreThan(dto.getMoreThan());
        entity.setType(dto.getType());
        entity.setNum(dto.getNum());
        entity.setCreateTime(LocalDateTime.now());
        if(save(entity)){
            return Result.success("创建成功");
        }
        return Result.fail("创建失败");
    }

    @Override
    public Result<?> update(Long shopId, Long goodsId, Long discountId, EditGoodsDiscountDto dto) {
        if(update(
                new UpdateWrapper<GoodsDiscountEntity>()
                        .eq("shop_id", shopId)
                        .eq("goods_id", goodsId)
                        .eq("id", discountId)
                        .set("more_than", dto.getMoreThan())
                        .set("num", dto.getNum())
                        .set("deadline", dto.getDeadline())
                        .set("type", dto.getType())
        )) return Result.success("修改成功");
        return Result.fail("修改失败");
    }
}
