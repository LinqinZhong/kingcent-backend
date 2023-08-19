package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:46
 */
@TableName("kc_shop_refund_order_map")
@Data
public class RefundOrderMapEntity {
    private Long refundOrderId;
    private Long orderId;
}
