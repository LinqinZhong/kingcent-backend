package com.kingcent.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WxLoginVo {
    private Long lid;
    private Long uid;
    private String secret;
}
