package com.kingcent.campus.entity.vo;

import lombok.Data;

@Data
public class AddressVo {
    private Long id;
    private Long siteId;
    private Long groupId;
    private Long pointId;
    private String name;
    private Integer gender;
    private String mobile;
    private Boolean isDefault;
    private String address;
}
