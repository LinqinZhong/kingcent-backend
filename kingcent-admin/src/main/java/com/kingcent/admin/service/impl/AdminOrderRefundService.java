package com.kingcent.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.entity.vo.RefundOrderVo;
import com.kingcent.admin.service.OrderRefundMapService;
import com.kingcent.admin.service.OrderGoodsService;
import com.kingcent.admin.service.OrderRefundService;
import com.kingcent.admin.service.OrderService;
import com.kingcent.common.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.constant.RefundReasons;
import com.kingcent.common.shop.constant.RefundStatus;
import com.kingcent.common.shop.entity.OrderGoodsEntity;
import com.kingcent.common.shop.entity.OrderRefundEntity;
import com.kingcent.common.shop.entity.OrderRefundMapEntity;
import com.kingcent.common.shop.mapper.OrderRefundMapper;
import com.kingcent.common.shop.util.BeanCopyUtils;
import com.kingcent.common.wx.entity.WxOrderGoodsEntity;
import com.kingcent.common.wx.service.WxRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:49
 */
@Service
public class AdminOrderRefundService extends ServiceImpl<OrderRefundMapper, OrderRefundEntity> implements OrderRefundService {

    /**
     * 微信退款服务
     */
    @Autowired
    private WxRefundService wxRefundService;

    /**
     * 退款服务
     */
    @Autowired
    @Lazy
    private OrderService orderService;


    @Autowired
    private OrderGoodsService orderGoodsService;


    @Autowired
    private OrderRefundMapService refundMapService;


    /**
     * 同意退款操作
     * 这个操作是商户被动发起的退款操作
     * @param shopId 商铺id（用于校验）
     * @param refundId 退款订单id
     * @param price 价格（用于确认）
     */
    @Override
    @Transactional
    public Result<?> confirmRefund(Long shopId, Long refundId, BigDecimal price) {
        //查找退款信息
        OrderRefundEntity refund = getById(refundId);
        if(refund == null){
            return Result.fail("退款订单不存在");
        }
        //查找相关订单编号
        List<OrderRefundMapEntity> refundOrderBinds = refundMapService.list(
                new QueryWrapper<OrderRefundMapEntity>().eq("refund_id", refundId)
        );
        List<Long> orderIds = new ArrayList<>();
        for (OrderRefundMapEntity bind : refundOrderBinds) {
            orderIds.add(bind.getOrderId());
        }
        //验证金额
        if(!refund.getRefund().equals(price)){
            return Result.fail("退款金额与订单金额不一致，请检查");
        }
        //查找商品
        List<OrderGoodsEntity> orderGoodsList = orderGoodsService.list(new QueryWrapper<OrderGoodsEntity>()
                .in("order_id", orderIds)
        );
        List<WxOrderGoodsEntity> goodsList = new ArrayList<>();
        for (OrderGoodsEntity g : orderGoodsList) {
            goodsList.add(new WxOrderGoodsEntity(
                    g.getSkuId()+"",
                    g.getTitle()+" "+g.getSkuInfo(),
                    g.getUnitPrice().multiply(new BigDecimal(100)).longValue(),
                    g.getPrice().subtract(g.getDiscount()).multiply(BigDecimal.valueOf(100)).longValue(),
                    g.getCount()
            ));
        }
        //更新退款订单状态为退款中
        if (!update(new UpdateWrapper<OrderRefundEntity>()
                .eq("id", refundId)
                .eq("status", refund.getStatus())
                .set("status", RefundStatus.PROCESSING)
        )) return Result.busy();

        //发起退款
        JSONObject res = wxRefundService.requestToRefund(
                refund.getTradeNo(),
                refund.getOutRefundNo(),
                refund.getRefund().multiply(new BigDecimal(100)).longValue(),
                refund.getOriginTotal().multiply(new BigDecimal(100)).longValue(),
                goodsList,
                RefundReasons.getReasonValue(refund.getReason())
        );
        if(!"SUCCESS".equals(res.get("status")) && !"PROCESSING".equals(res.get("status"))){
            //退款失败
            return Result.fail(JSONObject.toJSONString(res));
        }
        return Result.success();
    }

    @Override
    public VoList<RefundOrderVo> refundList(Long shopId, Integer pageNum, Integer status) {
        Page<OrderRefundEntity> page = new Page<>(pageNum, 10, true);
        QueryWrapper<OrderRefundEntity> w = new QueryWrapper<OrderRefundEntity>()
                .eq("shop_id", shopId)
                .orderByDesc("create_time");
        if(status != null) w.eq("status", status);
        page(page,w);
        List<OrderRefundEntity> list = page.getRecords();
        if (list.size() == 0){
            return new VoList<>(0,new ArrayList<>());
        }
        return new VoList<>((int) page.getTotal(), BeanCopyUtils.copyBeanList(list,RefundOrderVo.class));
    }

    @Override
    public Result<?> agree(Long shopId, Long refundId) {
        OrderRefundEntity refundEntity = getById(refundId);
        if(refundEntity == null || !refundEntity.getShopId().equals(shopId)){
            return Result.fail("退款订单不存在");
        }
        if(!refundEntity.getStatus().equals(RefundStatus.SUBMITTED)){
            return Result.fail("退款申请已失效");
        }
        if(update(new UpdateWrapper<OrderRefundEntity>()
                .eq("id", refundEntity.getId())
                .eq("status", refundEntity.getStatus())
                .set("status",RefundStatus.AGREED)
        )) return Result.success();
        return Result.busy();
    }
}
