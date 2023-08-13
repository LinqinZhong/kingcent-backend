package com.kingcent.campus.shop.entity.vo.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/11 7:04
 */
@Data
public class OrderStoreVo {
    private Long orderId;
    private Long shopId;
    private String shopName;
    private Integer status;
    private BigDecimal price;
    private BigDecimal payPrice;
    private BigDecimal goodsSumPrice;
    private BigDecimal deliveryFee;
    private String orderNo;
    private BigDecimal discount;
    private String address;
    private String receiverName;
    private String receiverMobile;
    private String payType;
    private String tradeNo;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime finishTime;
    private LocalDateTime paymentDeadline;
    private String remark;
    private List<OrderGoodsVo> goodsList;
}
