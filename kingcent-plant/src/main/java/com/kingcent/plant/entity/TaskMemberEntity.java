package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rainkyzhong
 * @date 2025/2/16 22:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("kc_agrc_task_member")
public class TaskMemberEntity {
    private Long taskId;
    private Long memberId;
}
