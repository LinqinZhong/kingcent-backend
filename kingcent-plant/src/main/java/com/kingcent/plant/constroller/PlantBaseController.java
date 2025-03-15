package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.FertilizerEntity;
import com.kingcent.plant.entity.PlantBaseEntity;
import com.kingcent.plant.service.PlantBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@RestController()
@RequestMapping("/plantBase")
public class PlantBaseController {

    @Autowired
    private PlantBaseService plantBaseService;

    @GetMapping("/{pageNum}/{pageSize}")
    public Result<Page<PlantBaseEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<PlantBaseEntity> page = plantBaseService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(@RequestBody PlantBaseEntity entity){
        return plantBaseService.addOrUpdate(entity);
    }


    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        return plantBaseService.delete(id);
    }
}
