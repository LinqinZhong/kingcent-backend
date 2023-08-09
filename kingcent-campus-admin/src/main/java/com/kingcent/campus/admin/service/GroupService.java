package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.GroupEntity;
import com.kingcent.campus.shop.entity.vo.group.GroupLocationVo;
import com.kingcent.campus.shop.service.impl.GroupServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class GroupService extends GroupServiceImpl {

}
