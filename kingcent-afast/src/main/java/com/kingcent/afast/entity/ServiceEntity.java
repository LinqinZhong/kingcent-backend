package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/14 23:07
 */
@Data
@TableName("kc_afast_service")
public class ServiceEntity {
    private Long id;
    private Long groupId;
    private String ecoId;
    private String name;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
