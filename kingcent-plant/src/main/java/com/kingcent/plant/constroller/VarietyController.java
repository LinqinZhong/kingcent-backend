package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.VarietyEntity;
import com.kingcent.plant.service.VarietyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@RestController()
@RequestMapping("/variety")
public class VarietyController {

    @Autowired
    private VarietyService varietyService;

    @GetMapping("/{pageNum}/{pageSize}")
    public Result<Page<VarietyEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<VarietyEntity> page = varietyService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(@RequestBody VarietyEntity varietyEntity){
        return varietyService.addOrUpdate(varietyEntity);
    }


    @DeleteMapping("/{varietyId}")
    public Result<?> delete(@PathVariable Long varietyId){
        return varietyService.delete(varietyId);
    }
}
