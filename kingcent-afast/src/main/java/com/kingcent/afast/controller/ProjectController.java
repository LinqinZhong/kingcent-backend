package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.entity.ProjectEntity;
import com.kingcent.afast.entity.RepositoryEntity;
import com.kingcent.afast.service.ProjectService;
import com.kingcent.afast.service.RepositoryService;
import com.kingcent.afast.vo.RepositoryBranchVo;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/13 16:01
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/list/{groupId}/{pageNum}/{pageSize}")
    public Result<Page<ProjectEntity>> list(
            HttpServletRequest request,
            @PathVariable Long groupId,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize
    ){
        return projectService.list(
                RequestUtil.getUserId(request),
                groupId,
                pageSize,
                pageNum
        );
    }
}
