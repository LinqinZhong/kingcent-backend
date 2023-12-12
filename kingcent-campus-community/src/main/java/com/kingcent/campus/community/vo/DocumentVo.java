package com.kingcent.campus.community.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2023/12/12 15:05
 */
@Data
public class DocumentVo {
    private Long id;
    private Long userId;
    private String avatar;
    private String topic;
    private String userName;
    private LocalDateTime createTime;
    private String title;
    private String description;
    private String content;
    private String images;
}
