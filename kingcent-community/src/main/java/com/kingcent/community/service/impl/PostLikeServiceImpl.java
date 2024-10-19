package com.kingcent.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.community.entity.PostLikeEntity;
import com.kingcent.community.mapper.PostLikeMapper;
import com.kingcent.community.service.PostLikeService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/12/13 8:16
 */
@Service
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLikeEntity> implements PostLikeService {
}
