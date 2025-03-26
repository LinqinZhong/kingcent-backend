package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@Data
@TableName("kc_agrc_land_water")
public class LandWaterEntity {
    private Long landId;
    private LocalDateTime createTime;
    private Double value;
    @TableField(exist = false)
    private Long hour;
}
