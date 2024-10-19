package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.dto.ProjectDaoDto;
import com.kingcent.afast.entity.ProjectDaoFuncEntity;
import com.kingcent.afast.service.ProjectDaoFuncService;
import com.kingcent.afast.vo.ProjectDaoFuncVo;
import com.kingcent.afast.vo.ProjectDaoVo;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2024/10/13 16:01
 */
@RestController
@RequestMapping("/projectDaoFunc")
public class ProjectDaoFuncController {

    @Autowired
    private ProjectDaoFuncService projectDaoFuncService;

    @PostMapping("/create/{projectId}/{daoId}")
    public Result<?> create(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long daoId,
            @RequestBody ProjectDaoFuncEntity entity
    ){
        return projectDaoFuncService.save(RequestUtil.getUserId(request),projectId,daoId,entity);
    }

    @GetMapping("/list/{projectId}/{daoId}/{pageNum}/{pageSize}")
    public Result<Page<ProjectDaoFuncVo>> list(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long daoId,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize
    ){
        return projectDaoFuncService.list(
                RequestUtil.getUserId(request),
                projectId,
                daoId,
                pageSize,
                pageNum
        );
    }

    @DeleteMapping("/delete/{projectId}/{daoId}")
    public Result<?> delete(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long daoId,
            @PathVariable Long funcId
    ){
        return projectDaoFuncService.delete(RequestUtil.getUserId(request),projectId, daoId,funcId);
    }
}
