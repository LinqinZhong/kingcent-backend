package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@Data
@TableName("kc_agrc_task")
public class TaskEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long planId;

    @TableField(exist = false)
    private String planName;

    private Long creatorMemberId;

    @TableField(exist = false)
    private String creatorMemberName;

    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String landIds;

    @TableField(exist = false)
    private String memberIds;

    private Integer type;

    private String content;

    @TableField(exist = false)
    private String landNames;

    @TableField(exist = false)
    private String memberNames;

}
