package com.kingcent.campus.shop.constant;


/**
 * 订单状态常量
 * @author rainkyzhong
 * @date 2023/8/16 8:35
 */
public class OrderStatus {
    //待支付
    public static final int NOT_PAY = 0;
    //待配送
    public static final int READY = 1;
    //配送中
    public static final int DELIVERING = 2;
    //已收货
    public static final int RECEIVED = 3;
    //已评价
    public static final int REVIEWED = 4;
    //已取消
    public static final int CLOSED = -1;
    //买家发起退款
    public static final int REQUEST_TO_REFUND = -2;
    //卖家同意退款
    public static final int AGREE_TO_REFUND = -3;
    //已退货
    public static final int BACKED = -4;
    //已退款
    public static final int REFUNDED = -5;
    //退款失败
    public static final int FAIL_TO_REFUND = -6;
}
