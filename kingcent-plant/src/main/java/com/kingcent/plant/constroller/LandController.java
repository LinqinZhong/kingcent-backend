package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.entity.result.Result;
import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.service.LandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@RestController()
@RequestMapping("/land")
public class LandController {

    @Autowired
    private LandService landService;

    @GetMapping("/{pageNum}/{pageSize}")
    public Result<Page<LandEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<LandEntity> page = landService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(@RequestBody LandEntity landEntity){
        return landService.add(landEntity);
    }
}
