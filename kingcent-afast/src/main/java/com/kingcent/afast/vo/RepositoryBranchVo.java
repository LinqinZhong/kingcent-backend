package com.kingcent.afast.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2024/10/15 0:15
 */
@Data
public class RepositoryBranchVo {
    private Long repoId;
    private Boolean isSymbolic;
    private Boolean isPeeled;
    private String name;
}
