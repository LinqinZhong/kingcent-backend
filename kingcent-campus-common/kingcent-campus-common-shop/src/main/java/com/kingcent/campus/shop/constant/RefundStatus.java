package com.kingcent.campus.shop.constant;


/**
 * 订单状态常量
 * @author rainkyzhong
 * @date 2023/8/16 8:35
 */
public class RefundStatus {
    //已提交申请
    public static final int SUBMITTED = 0;
    //卖家同意退款
    public static final int AGREED = 1;
    //买家已退货
    public static final int BACKED = 2;
    //卖家确认退款（等待第三方余额退还）
    public static final int PROCESSING = 3;
    //退款成功
    public static final int SUCCESS = 4;
    //已取消
    public static final int CANCEL = -1;
    //退款失败
    public static final int FAIL = -2;
}
