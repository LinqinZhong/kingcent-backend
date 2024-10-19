package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @author rainkyzhong
 * @date 2024/10/13 15:37
 */
@Data
@TableName("kc_afast_repository")
public class RepositoryEntity {
    private Long id;
    private Long groupId;
    private String name;
    private String url;
    private String publicKey;
    private String privateKey;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
