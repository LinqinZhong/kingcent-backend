package com.kingcent.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/12/13 8:15
 */
@Data
@TableName("kc_community_post_like")
public class PostLikeEntity {
    private Long userId;
    private Long postId;
}
