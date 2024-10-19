package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工程
 * @author rainkyzhong
 * @date 2024/10/13 12:38
 */
@Data
@TableName("kc_afast_project")
public class ProjectEntity {
    private Long id;
    private Long groupId;
    private Long repositoryId;
    private Long branchId;
    private String name;
    private Integer type;   //类型，0SpringBoot，1Vue3
    private String packageName; //包名
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
