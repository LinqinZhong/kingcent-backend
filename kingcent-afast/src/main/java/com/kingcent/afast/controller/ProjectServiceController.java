package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.dto.ProjectDaoDto;
import com.kingcent.afast.dto.ProjectServiceDto;
import com.kingcent.afast.service.ProjectServiceService;
import com.kingcent.afast.vo.ProjectDaoVo;
import com.kingcent.afast.vo.ProjectServiceVo;
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
@RequestMapping("/projectService")
public class ProjectServiceController {

    @Autowired
    private ProjectServiceService projectServiceService;

    @PostMapping("/create/{projectId}")
    public Result<?> create(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @RequestBody ProjectServiceDto serviceDto
    ){
        return projectServiceService.save(RequestUtil.getUserId(request),projectId,serviceDto);
    }

    @GetMapping("/list/{projectId}/{pageNum}/{pageSize}")
    public Result<Page<ProjectServiceVo>> list(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize
    ){
        return projectServiceService.list(
                RequestUtil.getUserId(request),
                projectId,
                pageSize,
                pageNum
        );
    }

    @DeleteMapping("/delete/{projectId}/{serviceId}")
    public Result<?> delete(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long serviceId
    ){
        return projectServiceService.delete(RequestUtil.getUserId(request),projectId, serviceId);
    }

    @GetMapping("/toJava/{projectId}/{serviceId}")
    public Result<?> toJava(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long serviceId
    ){
        return projectServiceService.generateJava(RequestUtil.getUserId(request),projectId, serviceId);
    }
}
