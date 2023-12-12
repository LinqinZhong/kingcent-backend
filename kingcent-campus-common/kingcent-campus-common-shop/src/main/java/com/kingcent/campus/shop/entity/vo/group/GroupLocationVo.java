package com.kingcent.campus.shop.entity.vo.group;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupLocationVo {
    private Long groupId;
    private String name;
    private Double longitude;
    private Double latitude;
}
