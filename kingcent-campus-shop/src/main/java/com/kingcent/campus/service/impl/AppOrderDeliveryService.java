package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.CarrierService;
import com.kingcent.campus.service.OrderDeliveryService;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.shop.constant.OrderDeliveryStatus;
import com.kingcent.campus.shop.constant.OrderStatus;
import com.kingcent.campus.shop.constant.PayType;
import com.kingcent.campus.shop.entity.CarrierEntity;
import com.kingcent.campus.shop.entity.OrderDeliveryEntity;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.mapper.OrderDeliveryMapper;
import com.kingcent.campus.wx.service.WxSubscribeMessageService;
import com.kingcent.campus.wx.service.WxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2023/8/24 20:02
 */
@Service
public class AppOrderDeliveryService extends ServiceImpl<OrderDeliveryMapper, OrderDeliveryEntity> implements OrderDeliveryService {
    @Autowired
    private CarrierService carrierService;
    @Autowired
    @Lazy
    private OrderService orderService;
    @Autowired
    private WxSubscribeMessageService wxSubscribeMessageService;

    @Autowired
    private WxUserService wxUserService;


    @Override
    @Transactional
    public Result<?> finish(Long userId, Long orderId, String code) {
        CarrierEntity carrier = carrierService.getOne(
                new QueryWrapper<CarrierEntity>()
                        .eq("user_id", userId)
        );
        if(carrier == null){
            return Result.fail("非配送员");
        }
        OrderDeliveryEntity delivery = getOne(new QueryWrapper<OrderDeliveryEntity>()
                .eq("order_id", orderId)
                .eq("carrier_id", carrier.getId())
        );
        if(delivery == null)
            return Result.fail("订单不存在");
        switch (delivery.getStatus()){
            case OrderDeliveryStatus.CANCEL:
            case OrderDeliveryStatus.MISS: return Result.fail("该订单已失效");
        }

        OrderEntity order = orderService.getById(orderId);
        switch (order.getStatus()){
            case OrderStatus.READY:
            case OrderStatus.DELIVERING:{
                break;
            }
            default: return Result.fail("该订单已失效");
        }

        //校验
        Result<?> check = orderService.checkReceiveCode(orderId, code);
        if(!check.getSuccess()){
            return check;
        }
        //更新配送订单、订单状态
        if(!update(new UpdateWrapper<OrderDeliveryEntity>()
                .eq("order_id",orderId)
                .eq("status", delivery.getStatus())
                .set("status", OrderDeliveryStatus.FINISH)
        ) || !orderService.update(
                new UpdateWrapper<OrderEntity>()
                        .eq("id", orderId)
                        .eq("status", order.getStatus())
                        .set("status", OrderStatus.ARRIVED)
                        .set("finish_time", LocalDateTime.now())
        )) return Result.busy();

        //发送送达通知
        if(order.getReceiveArriveMessage().equals(1)){
            if(order.getPayType().equals(PayType.WX_PAY)){
                try{
                    String openId = wxUserService.getWxOpenid(order.getUserId());
                    wxSubscribeMessageService.send(
                            "vVPPcwjWbpnhouKwkwF_xOm6VRHRAp9se8vEWfhWfLA",
                            "pages/order/order?ids=["+orderId+"]",
                            openId,
                            Map.of(
                                    "character_string1",Map.of("value",order.getOrderNo()),
                                    "thing4", Map.of("value", order.getReceiverName()),
                                    "time5", Map.of("value", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")))
                            ),
                            null,
                            null
                    );
                }catch (Exception e){
                    log.error("订单消息发送失败,{}", e);
                    e.printStackTrace();
                }
            }
        }
        return Result.success();
    }
}
