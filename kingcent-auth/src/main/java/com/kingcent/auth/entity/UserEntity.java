package com.kingcent.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("kc_user")
public class UserEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * wx_openid
     */
    private String wxOpenid;

    /**
     * password
     */
    private String password;

    /**
     * password_salt
     */
    private String passwordSalt;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * update_time
     */
    private Date updateTime;

    /**
     * is_deleted
     */
    @TableLogic
    private int isDeleted;

    /**
     * mobile
     */
    private String mobile;
}
