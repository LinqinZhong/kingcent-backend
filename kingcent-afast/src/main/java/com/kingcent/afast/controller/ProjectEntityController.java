package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.dto.ProjectEntityDto;
import com.kingcent.afast.entity.ProjectEntity;
import com.kingcent.afast.entity.ProjectEntityEntity;
import com.kingcent.afast.service.ProjectEntityService;
import com.kingcent.afast.service.ProjectService;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2024/10/13 16:01
 */
@RestController
@RequestMapping("/projectEntity")
public class ProjectEntityController {

    @Autowired
    private ProjectEntityService projectEntityService;

    @PostMapping("/create/{projectId}")
    public Result<?> create(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @RequestBody ProjectEntityDto entity
    ){
        return projectEntityService.save(RequestUtil.getUserId(request),projectId,entity);
    }

    @GetMapping("/list/{projectId}/{pageNum}/{pageSize}")
    public Result<Page<ProjectEntityEntity>> list(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize
    ){
        return projectEntityService.list(
                RequestUtil.getUserId(request),
                projectId,
                pageSize,
                pageNum
        );
    }

    @DeleteMapping("/delete/{projectId}/{entityId}")
    public Result<?> delete(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long entityId
    ){
        return projectEntityService.delete(RequestUtil.getUserId(request),projectId, entityId);
    }

    @GetMapping("/toSql/{projectId}/{entityId}")
    public Result<String> toSql(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long entityId
    ){
        return projectEntityService.toSql(RequestUtil.getUserId(request),projectId, entityId);
    }

    @GetMapping("/toJava/{projectId}/{entityId}")
    public Result<String> toJava(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long entityId
    ){
        return projectEntityService.toJava(RequestUtil.getUserId(request),projectId, entityId);
    }
}
