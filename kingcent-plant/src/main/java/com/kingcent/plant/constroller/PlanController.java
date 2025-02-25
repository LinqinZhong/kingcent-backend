package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import com.kingcent.plant.entity.PlanEntity;
import com.kingcent.plant.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@RestController()
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping("/{pageNum}/{pageSize}")
    public Result<Page<PlanEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<PlanEntity> page = planService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(HttpServletRequest request, @RequestBody PlanEntity planEntity){
        Long userId = RequestUtil.getUserId(request);
        return planService.addOrUpdate(userId, planEntity);
    }


    @DeleteMapping("/{planId}")
    public Result<?> delete(@PathVariable Long planId){
        return planService.delete(planId);
    }

    @GetMapping("/detail/{planId}")
    public Result<PlanEntity> detail(HttpServletRequest request, @PathVariable Long planId){
        Long userId = RequestUtil.getUserId(request);
        return Result.success(planService.detail(userId, planId));
    }
}
