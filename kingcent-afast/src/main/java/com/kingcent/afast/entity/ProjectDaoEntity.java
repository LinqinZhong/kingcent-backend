package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/16 16:06
 */
@Data
@TableName("kc_afast_project_dao")
public class ProjectDaoEntity {
    private Long id;
    private String name;
    private Long projectId;
    private Integer sourceType; //数据源类型，0自定义，1Mysql，2Redis，3文件
    private Long entityId;
    private String description;
    private LocalDateTime createTime;
}
