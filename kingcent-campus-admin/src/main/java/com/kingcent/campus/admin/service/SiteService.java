package com.kingcent.campus.admin.service;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.SiteEntity;
import com.kingcent.campus.shop.entity.vo.site.SiteLocationVo;
import com.kingcent.campus.shop.service.impl.SiteServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
@Slf4j
public class SiteService extends SiteServiceImpl {

}
