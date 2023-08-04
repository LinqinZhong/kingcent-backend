package com.kingcent.campus.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "kc_admin_login")
public class AdminLoginEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminId;
    private String secret;
    private LocalDateTime createTime;
    @TableLogic
    private Boolean isDeleted;
    private String ip;
}
