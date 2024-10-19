package com.kingcent.common.wx.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rainkyzhong
 * @date 2023/8/19 0:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxDeliveryShippingEntity {

    @JsonProperty("item_desc")
    private String itemDesc;
}
