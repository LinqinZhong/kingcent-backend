package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
    @TableId
    private Long id;
    private Long userId;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String username;
    private String mobile;
    private String email;
    @TableLogic
    private Boolean isDeleted;
    private Integer status;

    private String no;

    @TableField(exist = false)
    private String password;
    @TableField(exist = false)
    private String passwordSalt;

    private String avatar;

}
