package com.kingcent.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.community.dto.CreatePostDto;
import com.kingcent.community.entity.PostEntity;
import com.kingcent.community.vo.DocumentVo;

/**
 * @author rainkyzhong
 * @date 2023/12/12 14:39
 */
public interface PostService extends IService<PostEntity> {
    Result<VoList<DocumentVo>> list(Long userId, Integer page, Integer pageSize);

    Result<?> create(Long userId, CreatePostDto dto);

    Result<DocumentVo> get(Long userId, Long id);

    Result<?> like(Long userId, Long id);

    Result<?> delete(Long userId, Long id);
}
