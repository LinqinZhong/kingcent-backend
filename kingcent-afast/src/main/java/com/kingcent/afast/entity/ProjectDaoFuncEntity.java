package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/16 16:06
 */
@Data
@TableName("kc_afast_project_dao_func")
public class ProjectDaoFuncEntity {
    private Long id;
    private String name;
    private Long projectId;
    private Long daoId;
    private Long entityId;
    private String params;
    private String returnParam;
    private String description;
    private String execution;  //执行语句
    private Integer sourceType; //数据源类型（0mysql,1redis）
    private Integer executionType;  //执行类型 （0增1删2改3查）
    private LocalDateTime createTime;
}
