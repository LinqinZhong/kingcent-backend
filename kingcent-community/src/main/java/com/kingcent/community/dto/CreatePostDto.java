package com.kingcent.community.dto;

import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/12/13 0:35
 */
@Data
public class CreatePostDto {
    private String title;
    private String sortContent;
    private String content;
    private String images;
    //类型【0帖子 1文章】
    private Integer type;
}
