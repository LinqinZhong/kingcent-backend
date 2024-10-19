package com.kingcent.entity.vo.carrier;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/25 14:21
 */
@Data
public class DeliveryOrderVo {
    private Long id;
    private Long orderId;
    private Integer status;
    private String address;
    private String name;
    private String phone;
    private BigDecimal commission;
    private List<String> goods;
    private String remark;
}
