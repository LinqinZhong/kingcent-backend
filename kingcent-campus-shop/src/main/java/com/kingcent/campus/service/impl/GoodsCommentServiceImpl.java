package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.GoodsCommentCountEntity;
import com.kingcent.campus.common.entity.GoodsCommentEntity;
import com.kingcent.campus.common.entity.UserInfoEntity;
import com.kingcent.campus.entity.vo.goods.GoodsCommentVo;
import com.kingcent.campus.entity.vo.goods.GoodsDetailsCommentsPreviewVo;
import com.kingcent.campus.mapper.GoodsCommentMapper;
import com.kingcent.campus.service.GoodsCommentCountService;
import com.kingcent.campus.service.GoodsCommentService;
import com.kingcent.campus.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsCommentServiceImpl extends ServiceImpl<GoodsCommentMapper, GoodsCommentEntity> implements GoodsCommentService {


    @Autowired
    private GoodsCommentCountService commentCountService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 获取商品的评论数量信息
     * @param spuId 商品id
     */
    @Override
    public GoodsDetailsCommentsPreviewVo getGoodsDetailsCommentsPreview(Long spuId){

        //获取评论数量信息
        GoodsCommentCountEntity goodsCommentCount = commentCountService.getOne(new QueryWrapper<GoodsCommentCountEntity>().eq("goods_id",spuId));
        if(goodsCommentCount == null){
            return null;
        }

        int good = goodsCommentCount.getGood();
        int bad = goodsCommentCount.getBad();
        int mid = goodsCommentCount.getMid();

        //获取评论
        List<GoodsCommentEntity> commentEntities = list(new QueryWrapper<GoodsCommentEntity>().isNotNull("images").orderByDesc("score","create_time").last("limit 2"));
        //获取用户昵称和头像
        Set<Long> userIds = new HashSet<>();
        for (GoodsCommentEntity commentEntity : commentEntities) {
            userIds.add(commentEntity.getUserId());
        }
        Map<Long, UserInfoEntity> userInfo = userInfoService.userInfoMap(userIds);
        //整合数据
        List<GoodsCommentVo> commentsList = new ArrayList<>();
        for (GoodsCommentEntity commentEntity : commentEntities) {
            UserInfoEntity user  = userInfo.get(commentEntity.getUserId());
            String name = null;
            String head = null;
            if(user != null){
                name = user.getNickname();
                head = user.getAvatarUrl();
            }
            commentsList.add(new GoodsCommentVo(name,head,commentEntity));
        }
        return new GoodsDetailsCommentsPreviewVo(
                bad,
                mid,
                good,
                goodsCommentCount.getHasImage(),
                commentsList
        );
    }
}