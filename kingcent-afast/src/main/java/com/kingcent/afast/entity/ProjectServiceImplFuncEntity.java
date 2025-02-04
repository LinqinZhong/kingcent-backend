package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/11/3 14:02
 */
@Data
@TableName("kc_afast_project_service_impl_func")
public class ProjectServiceImplFuncEntity {
    private Long id;
    private String name;
    private Long projectId;
    private Long serviceId;
    private Long entityId;
    private Long implId;
    private Long funcId;
    private String params;
    private String returnParam;
    private String description;
    private LocalDateTime createTime;
    private Integer scope;
    private Integer type;
}
