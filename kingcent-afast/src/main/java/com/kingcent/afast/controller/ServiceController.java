package com.kingcent.afast.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.afast.entity.EcologyEntity;
import com.kingcent.afast.entity.ServiceEntity;
import com.kingcent.afast.service.EcologyService;
import com.kingcent.afast.service.ServiceService;
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
@RequestMapping("/service")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping("/list/{pageNum}/{pageSize}")
    public Result<Page<ServiceEntity>> list(
            HttpServletRequest request,
            @PathVariable Long pageNum,
            @PathVariable Long pageSize,
            @RequestParam(required = false) Long[] groupIds,
            @RequestParam(required = false) Long[] ecoIds
    ){
        return serviceService.list(
                RequestUtil.getUserId(request),
                pageSize,
                pageNum,
                groupIds,
                ecoIds
        );
    }



}
