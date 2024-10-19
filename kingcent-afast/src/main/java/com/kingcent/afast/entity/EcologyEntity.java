package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 生态
 * @author rainkyzhong
 * @date 2024/10/13 15:21
 */
@Data
@TableName("kc_afast_ecology")
public class EcologyEntity {
    private Long id;
    private Long groupId;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
}
