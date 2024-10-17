package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/16 16:06
 */
@Data
@TableName("kc_afast_project_entity")
public class ProjectEntityEntity {
    private Long id;
    private String name;
    private Long projectId;
    private String description;
    private String tableName;
    private String value;
    private LocalDateTime createTime;
}
