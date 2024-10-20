package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.entity.ProjectDaoFuncEntity;
import com.kingcent.afast.entity.ProjectServiceFuncEntity;
import com.kingcent.afast.service.ProjectServiceFuncService;
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
@RequestMapping("/projectServiceFunc")
public class ProjectServiceFuncController {

    @Autowired
    private ProjectServiceFuncService projectServiceFuncService;

    @PostMapping("/create/{projectId}/{serviceId}")
    public Result<?> create(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long serviceId,
            @RequestBody ProjectServiceFuncEntity entity
    ){
        return projectServiceFuncService.save(RequestUtil.getUserId(request),projectId,serviceId,entity);
    }

    @GetMapping("/list/{projectId}/{serviceId}/{pageNum}/{pageSize}")
    public Result<Page<ProjectServiceFuncEntity>> list(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long serviceId,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize
    ){
        return projectServiceFuncService.list(
                RequestUtil.getUserId(request),
                projectId,
                serviceId,
                pageNum,
                pageSize
        );
    }

    @DeleteMapping("/delete/{projectId}/{serviceId}")
    public Result<?> delete(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long serviceId,
            @PathVariable Long funcId
    ){
        return projectServiceFuncService.delete(RequestUtil.getUserId(request),projectId, serviceId,funcId);
    }
}
