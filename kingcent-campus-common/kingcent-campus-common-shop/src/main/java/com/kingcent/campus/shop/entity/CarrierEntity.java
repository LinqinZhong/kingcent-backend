package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author rainkyzhong
 * @date 2023/8/24 19:15
 */
@TableName("kc_shop_carrier")
@Data
public class CarrierEntity {
    private Long id;
    private Long userId;
    private Long shopId;
    private Integer activeDay;
    private String name;
    private String mobile;
    //时薪
    private BigDecimal hourSalaries;
    @TableLogic
    private Integer isDeleted;
}
