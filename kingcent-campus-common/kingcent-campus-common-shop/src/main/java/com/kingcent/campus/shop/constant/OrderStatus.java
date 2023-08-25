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
    //已送达
    public static final int ARRIVED = 3;
    //已收货
//    public static final int RECEIVED = 4;
    //已评价
    public static final int REVIEWED = 5;
    //已取消
    public static final int CLOSED = -1;
    //退款中
    public static final int REFUNDING = -2;
    //已退款
    public static final int REFUNDED = -3;
}
