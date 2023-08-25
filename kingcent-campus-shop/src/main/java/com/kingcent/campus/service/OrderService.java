package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.constant.PayType;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.vo.order.OrderStoreVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.listener.OrderListener;
import com.kingcent.campus.wx.entity.vo.CreateWxOrderResultVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface OrderService extends IService<OrderEntity> {

    void closeDeadOrder();

    boolean removeCloseOrderTask(Set<String> orderIds);

    @Transactional
    boolean closeOrder(List<Long> orderIds);

    VoList<OrderStoreVo> orderList(Long userId, Integer status, Integer page);

    <T> Result<T> checkOrder(Long userId, Integer orderNum);

    Result<?> pay(Long userId, Long loginId, Long orderId, String ipAddress);

    @Transactional
    Result<CreateWxOrderResultVo> createOrders(Long userId, Long loginId, PurchaseConfirmVo purchase, String ipAddress, String payType);

    List<OrderStoreVo> details(Long userId, List<Long> ids);

    @Transactional
    Result<?> requireRefund(Long userId, Long loginId, Long orderId, Integer reason, String message);

    Result<?> checkReceiveCode(Long orderId, String code);

    Result<String> setReceiveCode(Long userId, Long orderId, String code);
}
