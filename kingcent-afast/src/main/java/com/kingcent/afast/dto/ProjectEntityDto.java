package com.kingcent.afast.dto;

import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2024/10/16 21:59
 */
@Data
public class ProjectEntityDto {
    private Long id;
    private String name;
    private String value;
    private String tableName;
    private String description;
}
