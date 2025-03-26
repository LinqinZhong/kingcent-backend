package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@Data
@TableName("kc_agrc_land_ph")
public class LandPhEntity {
    private Long landId;
    private LocalDateTime createTime;
    private Double value;
    @TableField(exist = false)
    private Long hour;
}
