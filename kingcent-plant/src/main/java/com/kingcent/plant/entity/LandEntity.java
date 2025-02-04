package com.kingcent.plant.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@Data
public class LandEntity {
    private Long id;
    private String name;
    private String no;
    private BigDecimal area;
    private Integer status;
}
