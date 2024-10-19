package com.kingcent.afast.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/19 13:04
 */
@Data
public class ProjectDaoFuncVo {
    private Long id;
    private String name;
    private Long projectId;
    private Long daoId;
    private Long entityId;
    private String params;
    private String returnParam;
    private String description;
    private Integer sourceType;
    private String execution;
    private Integer executionType;
    private LocalDateTime createTime;
}
