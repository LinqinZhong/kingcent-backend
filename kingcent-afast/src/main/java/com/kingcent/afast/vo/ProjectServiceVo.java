package com.kingcent.afast.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/17 15:33
 */
@Data
public class ProjectServiceVo {
    private Long id;
    private String name;
    private Long projectId;
    private String entityName;
    private String daoName;
    private Long entityId;
    private Long daoId;
    private Integer countMethod;
    private String description;
    private LocalDateTime createTime;
}
