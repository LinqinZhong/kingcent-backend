package com.kingcent.campus.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2023/12/12 14:37
 */
@Data
@TableName("kc_community_document")
public class DocumentEntity {
    private Long id;
    private Long userId;
    private String topic;
    private LocalDateTime createTime;
    private String title;
    private String description;
    private String content;
    private String images;
    private Integer isDeleted;
}
