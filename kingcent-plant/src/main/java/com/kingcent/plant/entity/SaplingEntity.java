package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@TableName("kc_agrc_sapling")
public class SaplingEntity {
    private Long id;
    private String name;
    private Long varietyId;
    private Integer count;
    private Long creatorMemberId;
    @TableField(exist = false)
    private String creatorMemberName;
    @TableField(exist = false)
    private String varietyName;
    @TableField(exist = false)
    private String thumb;
    private LocalDateTime createTime;
}
