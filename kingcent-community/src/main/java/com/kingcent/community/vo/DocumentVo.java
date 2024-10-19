package com.kingcent.community.vo;

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
    private Integer type;
    private String label;
    private String topic;
    private String userName;
    private LocalDateTime createTime;
    private String title;
    private String content;
    private String images;
    private Boolean liked;
    private Integer countLike;
    private Integer countRead;
    private Boolean showDelete;
}
