package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.entity.EcologyEntity;
import com.kingcent.afast.entity.RepositoryEntity;
import com.kingcent.afast.service.EcologyService;
import com.kingcent.afast.service.RepositoryService;
import com.kingcent.afast.vo.RepositoryBranchVo;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/13 16:01
 */
@RestController
@RequestMapping("/repository")
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("/list/{groupId}/{pageNum}/{pageSize}")
    public Result<Page<RepositoryEntity>> list(
            HttpServletRequest request,
            @PathVariable Long groupId,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize
    ){
        return repositoryService.list(
                RequestUtil.getUserId(request),
                groupId,
                pageSize,
                pageNum
        );
    }

    @GetMapping("/branches/{repoId}")
    public Result<List<RepositoryBranchVo>> list(
            HttpServletRequest request,
            @PathVariable Long repoId
    ){
        return repositoryService.branches(
                RequestUtil.getUserId(request),
                repoId
        );
    }
}
