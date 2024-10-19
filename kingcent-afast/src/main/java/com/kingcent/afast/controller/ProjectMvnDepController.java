package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.entity.ProjectMvnDepEntity;
import com.kingcent.afast.service.ProjectMvnDepService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author rainkyzhong
 * @date 2024/10/13 16:01
 */
@RestController
@RequestMapping("/projectMvnDep")
public class ProjectMvnDepController {

    @Autowired
    private ProjectMvnDepService projectMvnDepService;

    @GetMapping("/list/{projectId}/{pageNum}/{pageSize}")
    public Result<Page<ProjectMvnDepEntity>> list(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize,
            @RequestParam(required = false) Integer status
    ){
        return projectMvnDepService.list(
                RequestUtil.getUserId(request),
                projectId,
                pageNum,
                pageSize,
                status
        );
    }
}
