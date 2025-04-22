package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.PesticideEntity;
import com.kingcent.plant.service.DamageService;
import com.kingcent.plant.service.PesticideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@RestController()
@RequestMapping("/damage")
public class DamageController {

    @Autowired
    private DamageService damageService;

    @GetMapping("/{pageNum}/{pageSize}")
    public Result<Page<DamageEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<DamageEntity> page = damageService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(@RequestBody DamageEntity damageEntity){
        return damageService.addOrUpdate(damageEntity);
    }


    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        return damageService.delete(id);
    }


    @GetMapping("/treatedBy/{id}")
    public Result<List<PesticideEntity>> getTreatedBy(@PathVariable Long id) {
        return Result.success(damageService.getTreatedBy(id));
    }
}
