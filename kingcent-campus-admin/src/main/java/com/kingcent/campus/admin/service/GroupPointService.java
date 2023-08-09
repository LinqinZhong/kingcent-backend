package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.shop.entity.GroupPointEntity;
import com.kingcent.campus.shop.service.impl.GroupPointServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class GroupPointService extends GroupPointServiceImpl {
}
