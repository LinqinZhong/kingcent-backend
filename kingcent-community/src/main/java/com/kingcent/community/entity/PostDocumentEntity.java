package com.kingcent.community.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/12/18 0:23
 */
@Data
@TableName("kc_community_post_document")
public class PostDocumentEntity {
    @TableId
    private Long postId;
    private String content;
}
