package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@Data
@TableName("kc_agrc_member")
public class MemberEntity {
    private Long id;
    private Long userId;
    private String name;
}
