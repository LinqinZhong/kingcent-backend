package com.kingcent.afast.entity;

import lombok.Data;

/**
 * 工件
 * @author rainkyzhong
 * @date 2024/10/13 15:36
 */
@Data
public class ArtifactEntity {
    private Long id;
    private Long ecoId;
    private Long ProjectId;
    private String version;
}
