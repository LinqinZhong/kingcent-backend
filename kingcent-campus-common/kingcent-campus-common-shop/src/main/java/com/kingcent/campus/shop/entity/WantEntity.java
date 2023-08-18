package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * “想要”
 * @author rainkyzhong
 * @date 2023/8/17 6:55
 */
@Data
@TableName("kc_shop_want")
public class WantEntity {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createTime;
    private Boolean isDeleted;
}
