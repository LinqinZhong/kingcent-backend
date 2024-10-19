package com.kingcent.common.wx.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rainkyzhong
 * @date 2023/8/18 23:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxDeliveryOrderKeyEntity {

    @JsonProperty("order_number_type")
    private Integer orderNumberType = 1;

    @JsonProperty("mchid")
    private String mchid;

    @JsonProperty("out_trade_no")
    private String outTradeNo;

}
