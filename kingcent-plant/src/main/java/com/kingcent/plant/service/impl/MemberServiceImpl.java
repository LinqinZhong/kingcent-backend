package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserEntity;
import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.entity.MemberEntity;
import com.kingcent.plant.mapper.MemberMapper;
import com.kingcent.plant.service.MemberService;
import com.kingcent.plant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author rainkyzhong
 * @date 2025/2/15 13:52
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, MemberEntity> implements MemberService {

    @Autowired
    private UserService userService;

    @Override
    public Page<MemberEntity> getPage(Integer pageNum, Integer pageSize, String nickname, String no, String username, String email, String mobile) {
        // 创建分页对象
        Page<MemberEntity> page = new Page<>(pageNum, pageSize);

        // 创建 Lambda 查询条件包装器
        LambdaQueryWrapper<MemberEntity> lambdaWrapper = new LambdaQueryWrapper<>();

        // 添加模糊查询条件
        if (nickname != null && !nickname.isEmpty()) {
            lambdaWrapper.like(MemberEntity::getName, nickname);
        }
        if (no != null && !no.isEmpty()) {
            lambdaWrapper.like(MemberEntity::getNo, no);
        }
        if (username != null && !username.isEmpty()) {
            lambdaWrapper.like(MemberEntity::getUsername, username);
        }
        if (email != null && !email.isEmpty()) {
            lambdaWrapper.like(MemberEntity::getEmail, email);
        }
        if (mobile != null && !mobile.isEmpty()) {
            lambdaWrapper.like(MemberEntity::getMobile, mobile);
        }

        // 执行分页查询
        page(page, lambdaWrapper);

        return page;
    }


    @Override
    @Transactional
    public Result<?> addOrUpdate(MemberEntity memberEntity) throws KingcentSystemException {
        boolean isUpdate = memberEntity.getId() != null;
        if(!isUpdate){
            memberEntity.setCreateTime(LocalDateTime.now());
        }
        memberEntity.setUpdateTime(LocalDateTime.now());
        memberEntity.setIsDeleted(false);
        saveOrUpdate(memberEntity);
        if(!isUpdate) {
            memberEntity.setNo("AG_" + memberEntity.getId());
            updateById(memberEntity);
            UserEntity userEntity = new UserEntity();
            userEntity.setPassword(memberEntity.getPassword());
            userEntity.setUsername(memberEntity.getUsername());
            userEntity.setPasswordSalt(memberEntity.getPasswordSalt());
            Result<UserEntity> userEntityResult = userService.create(userEntity);
            if (!userEntityResult.getSuccess()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return userEntityResult;
            }
            memberEntity.setUserId(userEntityResult.getData().getId());
            updateById(memberEntity);
        }
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }

    @Override
    public Result<MemberEntity> getByUserId(Long userId) {
        MemberEntity memberEntity = getOne(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getUserId, userId));
        if(memberEntity == null){
            return Result.fail("用户不是系统成员");
        }
        return Result.success(memberEntity);
    }
}
