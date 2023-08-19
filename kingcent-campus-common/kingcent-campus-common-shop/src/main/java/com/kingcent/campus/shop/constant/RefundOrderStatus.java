package com.kingcent.campus.shop.constant;


/**
 * 退款订单状态常量
 * @author rainkyzhong
 * @date 2023/8/16 8:35
 */
public class RefundOrderStatus {
    //等待商家同意
    public static final int WAIT = 0;
    //商家同意
    public static final int AGREE = 1;
    //已退款
    public static final int REFUNDED = 2;
    //买家取消
    public static final int CANCEL = -1;
    //商家拒绝
    public static final int REJECT = -2;
    //退款失败
    public static final int ERROR = -3;
}
