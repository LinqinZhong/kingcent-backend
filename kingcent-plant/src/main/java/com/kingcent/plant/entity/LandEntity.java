package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@Data
@TableName("kc_agrc_land")
public class LandEntity {
    private Long id;
    private String name;
    private String no;
    private BigDecimal area;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
