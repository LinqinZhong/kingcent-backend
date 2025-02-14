package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.TaskEntity;
import com.kingcent.plant.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@RestController()
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{pageNum}/{pageSize}")
    public Result<Page<TaskEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<TaskEntity> page = taskService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(@RequestBody TaskEntity taskEntity){
        return taskService.addOrUpdate(taskEntity);
    }


    @DeleteMapping("/{taskId}")
    public Result<?> delete(@PathVariable Long taskId){
        return taskService.delete(taskId);
    }
}
