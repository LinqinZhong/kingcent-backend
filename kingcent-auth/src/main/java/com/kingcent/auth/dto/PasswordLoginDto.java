package com.kingcent.auth.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class PasswordLoginDto {
    private UUID uuid;
    private String info;
}
