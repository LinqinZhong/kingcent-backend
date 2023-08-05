package com.kingcent.campus.common.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalTime;

@Data
@TableName("kc_shop_delivery_template")
public class DeliveryTemplateEntity {
    private Long id;
    private Long shopId;
    private LocalTime deliveryTime;
    private Integer restMonth;
    private Long restDay;
    private Integer restWeek;
    private Integer activeDays;
    private Boolean isUsed;
    private Integer reserveTime;
    private String name;
    @TableLogic
    private Boolean isDeleted;
}
