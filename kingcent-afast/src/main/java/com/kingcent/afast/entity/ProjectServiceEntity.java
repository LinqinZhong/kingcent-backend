package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/16 16:06
 */
@Data
@TableName("kc_afast_project_service")
public class ProjectServiceEntity {
    private Long id;
    private String name;
    private Long projectId;
    private Long entityId;
    private Long daoId;
    private String description;
    private LocalDateTime createTime;
}