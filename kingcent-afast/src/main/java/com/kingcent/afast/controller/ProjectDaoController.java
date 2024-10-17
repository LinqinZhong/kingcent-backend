package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.dto.ProjectDaoDto;
import com.kingcent.afast.dto.ProjectEntityDto;
import com.kingcent.afast.entity.ProjectDaoEntity;
import com.kingcent.afast.entity.ProjectEntityEntity;
import com.kingcent.afast.service.ProjectDaoService;
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
@RequestMapping("/projectDao")
public class ProjectDaoController {

    @Autowired
    private ProjectDaoService projectDaoService;

    @PostMapping("/create/{projectId}")
    public Result<?> create(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @RequestBody ProjectDaoDto entity
    ){
        return projectDaoService.save(RequestUtil.getUserId(request),projectId,entity);
    }

    @GetMapping("/list/{projectId}/{pageNum}/{pageSize}")
    public Result<Page<ProjectDaoVo>> list(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize
    ){
        return projectDaoService.list(
                RequestUtil.getUserId(request),
                projectId,
                pageSize,
                pageNum
        );
    }

    @DeleteMapping("/delete/{projectId}/{daoId}")
    public Result<?> delete(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long daoId
    ){
        return projectDaoService.delete(RequestUtil.getUserId(request),projectId, daoId);
    }
}
