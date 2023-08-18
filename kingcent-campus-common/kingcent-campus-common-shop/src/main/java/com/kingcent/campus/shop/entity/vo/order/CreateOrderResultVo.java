package com.kingcent.campus.shop.entity.vo.order;

import com.kingcent.campus.shop.entity.vo.payment.WxPaymentInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2023/8/9 1:01
 */
@Data
public class CreateOrderResultVo {
    private List<Long> orderIds;
    private WxPaymentInfo wxPaymentInfo;
}
