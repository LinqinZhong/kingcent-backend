package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.PesticideDamageEntity;
import com.kingcent.plant.entity.PesticideEntity;
import com.kingcent.plant.service.PesticideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@RestController()
@RequestMapping("/pesticide")
public class PesticideController {

    @Autowired
    private PesticideService pesticideService;

    @GetMapping("/{pageNum}/{pageSize}")
    public Result<Page<PesticideEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<PesticideEntity> page = pesticideService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(@RequestBody PesticideEntity entity){
        return pesticideService.addOrUpdate(entity);
    }


    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        return pesticideService.delete(id);
    }


    @PutMapping("/treat/{id}")
    public Result<?> treat(@PathVariable  Long id, @RequestParam Long damageId) throws KingcentSystemException {
        pesticideService.addPesticide(id, damageId);
        return Result.success();
    }

    @GetMapping("/treat/{id}")
    public Result<List<DamageEntity>> getTreat(@PathVariable Long id) {
        return Result.success(pesticideService.getTreat(id));
    }

    @DeleteMapping("/treat/{id}/{damageId}")
    public Result<?> deleteTreat(@PathVariable Long id, @PathVariable Long damageId) {
        pesticideService.deleteTreat(id,damageId);
        return Result.success();
    }

}
