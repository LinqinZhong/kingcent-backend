package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2024/10/19 23:52
 */
@Data
@TableName("kc_afast_project_mvn_dep")
public class ProjectMvnDepEntity {
    private Long id;
    private Long projectId;
    private String artifactId;
    private String groupId;
    private String version;
    private String scope;
    private Integer status;
}
