package com.kingcent.afast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kingcent.afast.constant.type.HostType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务器
 * @author rainkyzhong
 * @date 2024/10/13 12:40
 */
@Data
@TableName("kc_afast_server")
public class ServerEntity {
    private Long id;
    private String name;
    private Integer type;
    private Long groupId;
    private Long ip;
    private String port;
    private String username;
    private String password;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
