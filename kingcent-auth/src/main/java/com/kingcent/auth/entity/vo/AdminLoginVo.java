package com.kingcent.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminLoginVo {
    private Long uid;
    private Long lid;
    private String key;
    private String secret;
}
