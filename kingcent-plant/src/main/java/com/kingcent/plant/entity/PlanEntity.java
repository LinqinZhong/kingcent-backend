package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2025/2/6 0:51
 */
@Data
@TableName("kc_agrc_plan")
public class PlanEntity {
    private Long id;
    private String name;
    private String no;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime updateTime;
    private LocalDateTime endTime;
    private Integer status;
}
