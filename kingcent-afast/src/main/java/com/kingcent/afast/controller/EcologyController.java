package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.entity.EcologyEntity;
import com.kingcent.afast.service.EcologyService;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2024/10/13 16:01
 */
@RestController
@RequestMapping("/ecology")
public class EcologyController {

    @Autowired
    private EcologyService ecologyService;

    @GetMapping("/list/{pageNum}/{pageSize}")
    public Result<Page<EcologyEntity>> list(
            HttpServletRequest request,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize,
            @RequestParam(required = false) Long[] groupIds
    ){
        return ecologyService.list(
                RequestUtil.getUserId(request),
                pageSize,
                pageNum,
                groupIds
        );
    }
}
