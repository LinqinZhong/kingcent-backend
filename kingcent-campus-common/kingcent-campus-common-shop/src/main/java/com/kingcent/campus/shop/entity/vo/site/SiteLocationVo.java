package com.kingcent.campus.shop.entity.vo.site;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SiteLocationVo {
    private Long siteId;
    private String name;
    private Double longitude;
    private Double latitude;
}