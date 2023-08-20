package com.kingcent.campus.wx.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rainkyzhong
 * @date 2023/8/18 18:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxPaymentInfoVo {
    private String appId;
    private String timeStamp;
    private String nonceStr;
    @JsonProperty("package")
    private String packageStr;
    private String signType;
    private String paySign;

}
