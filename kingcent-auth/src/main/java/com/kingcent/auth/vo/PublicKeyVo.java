package com.kingcent.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PublicKeyVo {
    private UUID uuid;
    private String key;
}
