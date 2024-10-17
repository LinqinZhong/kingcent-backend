package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.entity.ServerEntity;
import com.kingcent.afast.service.ServerService;
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
@RequestMapping("/server")
public class ServerController {

    @Autowired
    private ServerService serverService;

    @GetMapping("/list/{pageNum}/{pageSize}")
    public Result<Page<ServerEntity>> list(
            HttpServletRequest request,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize,
            @RequestParam(required = false) Long[] groupIds
    ){
        return serverService.list(
                RequestUtil.getUserId(request),
                pageSize,
                pageNum,
                groupIds
        );
    }



}
