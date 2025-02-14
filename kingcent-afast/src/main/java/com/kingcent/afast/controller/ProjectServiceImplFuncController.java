package com.kingcent.afast.controller;

import com.kingcent.afast.entity.ProjectServiceFuncEntity;
import com.kingcent.afast.entity.ProjectServiceImplFuncEntity;
import com.kingcent.afast.service.ProjectServiceImplFuncService;
import com.kingcent.common.result.Result;
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
@RequestMapping("/projectServiceImplFunc")
public class ProjectServiceImplFuncController {

    @Autowired
    private ProjectServiceImplFuncService projectServiceImplFuncService;


    @GetMapping("/list/{projectId}/{implId}")
    public Result<List<ProjectServiceImplFuncEntity>> list(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long implId
    ){
        return projectServiceImplFuncService.list(
                RequestUtil.getUserId(request),
                projectId,
                implId
        );
    }

    @GetMapping("/unimplementedList/{projectId}/{implId}")
    public Result<List<ProjectServiceFuncEntity>> unimplementedList(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long implId
    ){
        return projectServiceImplFuncService.unimplementedFunctions(
                RequestUtil.getUserId(request),
                projectId,
                implId
        );
    }

    @PostMapping("/setImplement/{projectId}/{implId}")
    public Result<?> setImplement(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long implId,
            @RequestBody List<Long> ids
    ){
        return projectServiceImplFuncService.createImplements(
                RequestUtil.getUserId(request),
                projectId,
                implId,
                ids
        );
    }
}
