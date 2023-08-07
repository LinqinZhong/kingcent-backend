package com.kingcent.campus.shop.entity.vo.group;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GroupLocationVo {
    private Long groupId;
    private String name;
    private Double longitude;
    private Double latitude;
    private Double distance;
}
