package com.kingcent.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.community.dto.CreatePostDto;
import com.kingcent.community.entity.PostDocumentEntity;
import com.kingcent.community.entity.PostEntity;
import com.kingcent.community.entity.PostLikeEntity;
import com.kingcent.community.mapper.PostMapper;
import com.kingcent.community.service.PostDocumentService;
import com.kingcent.community.service.PostLikeService;
import com.kingcent.community.service.PostService;
import com.kingcent.community.service.UserService;
import com.kingcent.community.vo.DocumentVo;
import com.kingcent.common.user.entity.UserInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/12/12 14:40
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, PostEntity> implements PostService {

    @Autowired
    private UserService userService;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostDocumentService postDocumentService;

    @Override
    public Result<VoList<DocumentVo>> list(Long userId, Integer page, Integer pageSize){
        if(page == null) page = 1;
        if(pageSize == null) pageSize = 5;
        Page<PostEntity> p = new Page<>(page, pageSize,true);
        Page<PostEntity> res = page(p, new QueryWrapper<PostEntity>()
                .select("id, user_id, title, type, topic, content, images, count_read, count_like, create_time")
                .orderByDesc("create_time")
        );

        if(res.getRecords().size() == 0){
            return Result.success(new VoList<>(0, new ArrayList<>()));
        }

        //获取用户点赞记录
        List<Long> likedId = new ArrayList<>();
        if(userId != null) {
            List<Long> ids = new ArrayList<>();
            for (PostEntity record : res.getRecords()) {
                ids.add(record.getId());
            }
            List<PostLikeEntity> likes = postLikeService.list(
                    new QueryWrapper<PostLikeEntity>()
                            .eq("user_id", userId)
                            .in("post_id", ids)
            );
            for (PostLikeEntity like : likes) {
                likedId.add(like.getPostId());
            }
        }

        //获取用户信息
        Set<Long> userIds = new HashSet<>();
        for (PostEntity record : res.getRecords()) {
            userIds.add(record.getUserId());
        }
        Map<Long, UserInfoEntity> userInfoMap = userService.userInfoMap(userIds);

        List<DocumentVo> records = new ArrayList<>();
        for (PostEntity record : res.getRecords()) {
            DocumentVo vo = new DocumentVo();
            vo.setId(record.getId());
            vo.setTopic(record.getTopic());
            vo.setContent(record.getContent());
            vo.setImages(record.getImages());
            vo.setTitle(record.getTitle());
            vo.setType(record.getType());
            vo.setLiked(likedId.contains(record.getId()));
            vo.setCreateTime(record.getCreateTime());
            vo.setCountRead(record.getCountRead());
            vo.setCountLike(record.getCountLike());
            vo.setShowDelete(Objects.equals(userId, record.getUserId()));
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
    @Transactional
    public Result<?> create(Long userId, CreatePostDto dto) {
        PostEntity entity = new PostEntity();
        entity.setTitle(dto.getTitle());
        entity.setUserId(userId);
        entity.setImages(dto.getImages());
        entity.setCreateTime(LocalDateTime.now());
        entity.setType(dto.getType());
        //文章的短内容作为内容
        if(entity.getType() == 1){
            entity.setContent(dto.getSortContent());
        }
        //其它类型的发表以内容作为内容
        else{
            entity.setContent(dto.getContent());
        }
        if(save(entity)){
            if(dto.getType() == 1){
                PostDocumentEntity postDocument = new PostDocumentEntity();
                postDocument.setPostId(entity.getId());
                postDocument.setContent(dto.getContent());
                if(postDocumentService.save(postDocument)){
                    return Result.success();
                }
            }else{
                return Result.success();
            }
        }
        return Result.fail("保存失败");
    }

    private void addRead(Long id){
        update(new UpdateWrapper<PostEntity>()
                .eq("id", id)
                .setSql("count_read = count_read + 1")
        );
    }

    @Override
    public Result<DocumentVo> get(Long userId, Long id) {
        PostEntity entity = getById(id);
        if (entity != null){

            PostLikeEntity like = null;
            if(userId != null) {
                like = postLikeService.getOne(
                        new QueryWrapper<PostLikeEntity>()
                                .eq("post_id", id)
                                .eq("user_id", userId)
                );
            }

            DocumentVo vo = new DocumentVo();
            vo.setTitle(entity.getTitle());
            vo.setImages(entity.getImages());
            vo.setId(entity.getId());
            vo.setLiked(like != null);
            vo.setCreateTime(entity.getCreateTime());
            vo.setCountRead(entity.getCountRead()+1);
            vo.setCountLike(entity.getCountLike());
            vo.setShowDelete(Objects.equals(userId, entity.getUserId()));

            //文章
            if(entity.getType() == 1){
                PostDocumentEntity document = postDocumentService.getById(vo.getId());
                if(document != null) vo.setContent(document.getContent());
            }

            addRead(id);
            return Result.success(vo);
        }
        return Result.fail("文章不存在");
    }


    @Override
    public Result<?> like(Long userId, Long id) {
        PostLikeEntity like = postLikeService.getOne(
                new QueryWrapper<PostLikeEntity>()
                        .eq("post_id", id)
                        .eq("user_id", userId)
        );
        if(like != null){
            return Result.fail("你已经点赞过了");
        }
        PostEntity postEntity = getById(id);
        if(postEntity == null){
            return Result.fail("文章不存在");
        }
        update(new UpdateWrapper<PostEntity>()
                .eq("id", id)
                .setSql("count_like = count_like + 1")
        );
        like = new PostLikeEntity();
        like.setPostId(id);
        like.setUserId(userId);
        postLikeService.save(like);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long userId, Long id) {
        if(remove(new QueryWrapper<PostEntity>()
                .eq("id", id)
                .eq("user_id", userId)
        )){
            return Result.success();
        }
        return Result.fail("文章不存在");
    }
}
