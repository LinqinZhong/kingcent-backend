package com.kingcent.campus.entity.vo.carrier;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/9/1 20:58
 */
@Data
public class CarrierDeliveryVo {
    private Long dayOrderCount;
    private Long deliveredOrderCount;
    private Long mouthDeliveredOrderCount;
    private BigDecimal dayCommission;
    private BigDecimal monthCommission;
    private List<DeliveryOrderVo> orders;
}
