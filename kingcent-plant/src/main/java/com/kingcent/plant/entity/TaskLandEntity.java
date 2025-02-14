package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("kc_agrc_task_land")
@AllArgsConstructor
@NoArgsConstructor
public class TaskLandEntity {
    private Long taskId;
    private Long landId;
}
