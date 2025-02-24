package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("kc_agrc_task_comment")
public class TaskCommentEntity {

    @TableId
    private Long id;
    private Long taskId;
    private Long userId;
    private Long memberId;
    private String content;
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
