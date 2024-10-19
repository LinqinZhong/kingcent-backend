package com.kingcent.afast.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/17 15:33
 */
@Data
public class ProjectDaoVo {
    private Long id;
    private String name;
    private Long projectId;
    private String entityName;
    private Long entityId;
    private Integer countMethod;
    private Integer sourceType;
    private String description;
    private LocalDateTime createTime;
}
