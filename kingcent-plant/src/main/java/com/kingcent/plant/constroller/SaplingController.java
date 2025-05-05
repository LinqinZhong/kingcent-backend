package com.kingcent.plant.constroller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import com.kingcent.plant.entity.SaplingEntity;
import com.kingcent.plant.entity.VarietyEntity;
import com.kingcent.plant.entity.WeatherEntity;
import com.kingcent.plant.entity.WeatherForecastEntity;
import com.kingcent.plant.service.SaplingService;
import com.kingcent.plant.service.VarietyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/23 13:27
 */
@RequestMapping("/sapling")
@Controller
public class SaplingController {

    @Autowired
    private SaplingService saplingService;

    @GetMapping("/{pageNum}/{pageSize}")
    @ResponseBody
    public Result<Page<SaplingEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<SaplingEntity> page = saplingService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(HttpServletRequest request,  @RequestBody SaplingEntity sapling){
        Long userId = RequestUtil.getUserId(request);
        return saplingService.addOrUpdate(userId, sapling);
    }


    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        return saplingService.delete(id);
    }
}
