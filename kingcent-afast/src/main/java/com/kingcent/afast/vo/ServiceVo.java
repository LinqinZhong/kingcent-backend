package com.kingcent.afast.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/15 0:15
 */
@Data
public class ServiceVo {
    private Long id;
    private Long groupId;
    private String ecoId;
    private String ecoName;
    private String name;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
