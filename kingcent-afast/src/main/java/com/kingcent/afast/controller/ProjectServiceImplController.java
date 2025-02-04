package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.entity.ProjectServiceImplEntity;
import com.kingcent.afast.service.ProjectServiceImplService;
import com.kingcent.afast.vo.ProjectServiceVo;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/13 16:01
 */
@RestController
@RequestMapping("/projectServiceImpl")
public class ProjectServiceImplController {

    @Autowired
    private ProjectServiceImplService projectServiceImplService;

    @PostMapping("/create/{projectId}/{serviceId}")
    public Result<?> create(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long serviceId,
            @RequestBody ProjectServiceImplEntity serviceImpl
    ){
        return projectServiceImplService.save(RequestUtil.getUserId(request),projectId,serviceId,serviceImpl);
    }

    @GetMapping("/list/{projectId}/{serviceId}")
    public Result<List<ProjectServiceImplEntity>> list(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long serviceId
    ){
        return projectServiceImplService.list(
                RequestUtil.getUserId(request),
                projectId,
                serviceId
        );
    }

    @DeleteMapping("/delete/{projectId}/{implId}")
    public Result<?> delete(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long implId
    ){
        return projectServiceImplService.delete(RequestUtil.getUserId(request),projectId, implId);
    }
}
