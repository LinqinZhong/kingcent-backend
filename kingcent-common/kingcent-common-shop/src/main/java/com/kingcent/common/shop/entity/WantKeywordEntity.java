package com.kingcent.common.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * “想要”关键词
 * @author rainkyzhong
 * @date 2023/8/17 6:55
 */
@TableName("kc_shop_want_keyword")
@Data
public class WantKeywordEntity {
    private Long id;
    private Long groupId;
    private String value;
    private Integer count;
}
