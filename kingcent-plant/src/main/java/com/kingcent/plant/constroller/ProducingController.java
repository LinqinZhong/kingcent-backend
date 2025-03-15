package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.ProducingEntity;
import com.kingcent.plant.service.ProducingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@RestController()
@RequestMapping("/producing")
public class ProducingController {

    @Autowired
    private ProducingService producingService;

    @GetMapping("/{pageNum}/{pageSize}")
    public Result<Page<ProducingEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<ProducingEntity> page = producingService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(@RequestBody ProducingEntity producingEntity){
        return producingService.addOrUpdate(producingEntity);
    }


    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        return producingService.delete(id);
    }
}
