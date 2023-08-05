package com.kingcent.campus.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("kc_shop_pay_type")
@Data
public class PayTypeEntity {
    private Long shopId;
    private String type;
    private Boolean enabled;
}
