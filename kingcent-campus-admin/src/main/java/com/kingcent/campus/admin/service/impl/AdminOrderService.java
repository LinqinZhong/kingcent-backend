package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.CarrierService;
import com.kingcent.campus.admin.service.OrderGoodsService;
import com.kingcent.campus.admin.service.OrderService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.constant.OrderStatus;
import com.kingcent.campus.shop.entity.CarrierEntity;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.OrderGoodsEntity;
import com.kingcent.campus.shop.mapper.OrderMapper;
import com.kingcent.campus.wx.service.WxShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author rainkyzhong
 */
@Service
@Slf4j
public class AdminOrderService extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Autowired
    private WxShippingService shippingService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private CarrierService carrierService;

    @Override
    public Result<VoList<OrderEntity>> list(Integer page, Integer pageSize, Long shopId) {
        Page<OrderEntity> p = new Page<>(page, pageSize, true);
        Page<OrderEntity> res = page(p);
        return Result.success(new VoList<>((int) res.getTotal(), res.getRecords()));
    }

    @Override
    public Result<?> setDiscount(Long shopId, Long orderId, BigDecimal discount) {
        if(discount == null || discount.doubleValue() < 0) return Result.fail("优惠金额不能小于0");
        OrderEntity order = getOne(new QueryWrapper<OrderEntity>()
                .eq("id", orderId)
                .eq("shop_id", shopId)
        );
        if(order.getStatus() != 0){
            return Result.fail("订单已支付或已关闭");
        }
        order.setDiscount(discount);
        order.setPayPrice(order.getPrice().subtract(discount));
        if (updateById(order)) {
            return Result.success("修改成功");
        }
        return Result.fail("修改失败");
    }


    /**
     * 分配订单
     */
//    @Override
//    @Transactional
//    public Result<List<OrderEntity>> assignOrder(Long shopId) {
//
//        //今天结束时间
//        //查出今天所有的配送订单（按pointId排序，单次配送20条）
//        List<OrderEntity> list = list(new QueryWrapper<OrderEntity>()
//                .eq("shop_id", shopId)
//                .le("delivery_time", end)
//                .in("status", List.of(
//                        OrderStatus.READY,  //待配送
//                        OrderStatus.DELIVERING  //配送中
//                ))
//                .orderByDesc("point_id")
//                .last("limit "+count)
//        );
//
//        if (list.size() == 0){
//            return Result.fail("没有要配送的订单");
//        }
//
//        //提取商品编号
//        Set<Long> orderIds = new HashSet<>();
//        for (OrderEntity order : list) {
//            orderIds.add(order.getId());
//        }
//        //获取商品列表
//        Map<Long, List<OrderGoodsEntity>> goodsOfOrderMap = new HashMap<>();
//        List<OrderGoodsEntity> goodsList = orderGoodsService.list(new QueryWrapper<OrderGoodsEntity>()
//                .in("order_id", orderIds)
//        );
//        for (OrderGoodsEntity goods : goodsList) {
//            List<OrderGoodsEntity> ogList = goodsOfOrderMap.computeIfAbsent(goods.getOrderId(), a -> new ArrayList<>());
//            ogList.add(goods);
//        }
//
//
//        int countReady = 0;
//        //上传发货信息
//        for (OrderEntity order : list) {
//            //上传过的无需再次上传
//            if(order.getStatus().equals(OrderStatus.DELIVERING)) continue;
//            countReady ++;
//            //商品信息
//            List<OrderGoodsEntity> ogList = goodsOfOrderMap.get(order.getId());
//            int len = 0;
//            List<String> goodsNames = new ArrayList<>();
//            int orderCount = ogList.size();
//            for (int i = 0; i < orderCount; i++) {
//                if (len >= 110) {
//                    break;  //微信限制最多只能120个字
//                }
//                OrderGoodsEntity g = goodsList.get(i);
//                String goodsName = g.getTitle()+" "+g.getSkuInfo();
//                len += goodsName.length()+1;
//                goodsNames.add(goodsName);
//            }
//            String desc = String.join(",", goodsNames);
//            if (len > 110) desc += "...";
//            shippingService.upload(order.getUserId(),order.getOrderNo(),desc);
//        }
//
//        //更新订单状态
//        UpdateWrapper<OrderEntity> wrapper = new UpdateWrapper<>();
//        wrapper.in("id", orderIds);
//        wrapper.eq("status", OrderStatus.READY);
//        wrapper.set("status", OrderStatus.DELIVERING);
//        int updated = baseMapper.update(null, wrapper);
//        if(updated != countReady){
//            return Result.busy();
//        }
//
//        return Result.success(list);
//    }
}
