package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.SiteEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.site.SiteLocationVo;

import java.util.List;

public interface SiteService extends IService<SiteEntity> {
    Result<List<SiteLocationVo>> fetchNearestSite(Double longitude, Double latitude);

    void initSiteLocations();
}
