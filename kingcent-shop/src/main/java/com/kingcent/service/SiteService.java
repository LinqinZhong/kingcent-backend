package com.kingcent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.SiteEntity;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.shop.entity.vo.site.SiteLocationVo;

public interface SiteService extends IService<SiteEntity> {
//    Result<List<SiteLocationVo>> fetchNearestSite(Double longitude, Double latitude);
    Result<VoList<SiteLocationVo>> search(Integer page, Integer pageSize, String keywords);
//    void initSiteLocations();
}