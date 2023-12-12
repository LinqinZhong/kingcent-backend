package com.kingcent.campus.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.community.dto.CreateDocumentDto;
import com.kingcent.campus.community.entity.DocumentEntity;
import com.kingcent.campus.community.mapper.DocumentMapper;
import com.kingcent.campus.community.service.DocumentService;
import com.kingcent.campus.community.service.UserService;
import com.kingcent.campus.community.vo.DocumentVo;
import com.kingcent.campus.user.entity.UserInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/12/12 14:40
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, DocumentEntity> implements DocumentService {

    @Autowired
    private UserService userService;

    @Override
    public Result<VoList<DocumentVo>> list(Integer page, Integer pageSize){
        if(page == null) page = 1;
        if(pageSize == null) pageSize = 5;
        Page<DocumentEntity> p = new Page<>(page, pageSize,true);
        Page<DocumentEntity> res = page(p, new QueryWrapper<DocumentEntity>()
                .select("id, user_id, title, topic, description, images, create_time")
                .orderByDesc("create_time")
        );

        //获取用户信息
        Set<Long> userIds = new HashSet<>();
        for (DocumentEntity record : res.getRecords()) {
            userIds.add(record.getUserId());
        }
        Map<Long, UserInfoEntity> userInfoMap = userService.userInfoMap(userIds);

        List<DocumentVo> records = new ArrayList<>();
        for (DocumentEntity record : res.getRecords()) {
            DocumentVo vo = new DocumentVo();
            vo.setId(record.getId());
            vo.setTopic(record.getTopic());
            vo.setDescription(record.getDescription());
            vo.setImages(record.getImages());
            vo.setTitle(record.getTitle());
            vo.setCreateTime(record.getCreateTime());

            UserInfoEntity userInfo = userInfoMap.get(record.getUserId());
            if(userInfo != null){
                vo.setUserName(userInfo.getNickname());
                vo.setAvatar(userInfo.getAvatarUrl());
            }

            records.add(vo);
        }
        return Result.success(new VoList<>((int) res.getTotal(), records));
    }

    @Override
    public Result<?> create(Long userId, CreateDocumentDto dto) {
        DocumentEntity entity = new DocumentEntity();
        entity.setContent(dto.getContent());
        entity.setTitle(dto.getTitle());
        entity.setUserId(userId);
        entity.setImages(dto.getImages());
        entity.setCreateTime(LocalDateTime.now());
        entity.setDescription(dto.getDescription());
        if(save(entity)){
            return Result.success();
        }
        return Result.fail("保存失败");
    }


}
