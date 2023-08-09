package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.SiteEntity;
import com.kingcent.campus.shop.entity.vo.site.SiteLocationVo;
import com.kingcent.campus.shop.mapper.SiteMapper;
import com.kingcent.campus.shop.service.SiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class SiteServiceImpl extends ServiceImpl<SiteMapper, SiteEntity> implements SiteService {

    @Override
    public Result<List<SiteLocationVo>> fetchNearestSite(Double longitude, Double latitude) {
        return null;
    }

    @Override
    public void initSiteLocations() {

    }
}
