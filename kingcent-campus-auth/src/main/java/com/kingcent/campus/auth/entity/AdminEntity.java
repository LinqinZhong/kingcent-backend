package com.kingcent.campus.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_admin")
public class AdminEntity {
    private Long id;
    private String username;
    private String password;
    private String salt;
    private String secret;
}
