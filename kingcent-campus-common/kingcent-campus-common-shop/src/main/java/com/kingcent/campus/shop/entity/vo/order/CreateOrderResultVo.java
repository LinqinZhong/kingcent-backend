package com.kingcent.campus.shop.entity.vo.order;

import lombok.Data;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/9 1:01
 */
@Data
public class CreateOrderResultVo {
    private List<Long> orderIds;
    private Boolean needPay;
}
