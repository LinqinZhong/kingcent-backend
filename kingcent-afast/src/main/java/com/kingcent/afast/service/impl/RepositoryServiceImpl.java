package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.dto.RepositoryDto;
import com.kingcent.afast.entity.RepositoryEntity;
import com.kingcent.afast.mapper.RepositoryMapper;
import com.kingcent.afast.service.RepositoryService;
import com.kingcent.afast.utils.GitUtil;
import com.kingcent.afast.vo.RepositoryBranchVo;
import com.kingcent.common.entity.result.Result;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/16 1:59
 */
@Service
public class RepositoryServiceImpl extends ServiceImpl<RepositoryMapper, RepositoryEntity> implements RepositoryService {
    @Override
    public void create(RepositoryDto repository) {

    }

    @Override
    public Result<Page<RepositoryEntity>> list(Long userId, Long groupId, Long pageSize, Long pageNum) {
        LambdaQueryWrapper<RepositoryEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepositoryEntity::getGroupId,groupId);
        return Result.success("",page(new Page<>(pageNum,pageSize),wrapper));
    }

    @Override
    public Result<List<RepositoryBranchVo>> branches(Long userId, Long repoId){
        RepositoryEntity repository = getById(repoId);
        if(repository == null) return Result.fail("仓库不存在");
        Collection<Ref> branches = GitUtil.branches(repository.getPrivateKey(), repository.getUrl());
        List<RepositoryBranchVo> res = new ArrayList<>();
        for (Ref branch : branches) {
            if (branch.getName().startsWith("refs/heads/")) {
                String branchName = branch.getName().replace("refs/heads/", "");
                RepositoryBranchVo branchVo = new RepositoryBranchVo();
                res.add(branchVo);
                branchVo.setName(branchName);
                branchVo.setRepoId(repoId);
                branchVo.setIsSymbolic(branch.getTarget().isSymbolic());
                branchVo.setIsPeeled(branch.isPeeled());
            }
        }
        return Result.success(res);
    }
}
