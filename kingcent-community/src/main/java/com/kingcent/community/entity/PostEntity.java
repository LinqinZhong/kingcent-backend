package com.kingcent.community.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2023/12/12 14:37
 */
@Data
@TableName("kc_community_post")
public class PostEntity {
    @TableId
    private Long id;
    private Long userId;
    //类型0帖子 1文章
    private Integer type;
    private String topic;
    private LocalDateTime createTime;
    private String title;
    private String content;
    private String images;
    private Integer countRead;
    private Integer countLike;
    private Integer isDeleted;
}
