package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import com.kingcent.plant.entity.TaskEntity;
import com.kingcent.plant.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Update;
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
    public Result<?> add(HttpServletRequest request, @RequestBody TaskEntity taskEntity){
        Long userId = RequestUtil.getUserId(request);
        return taskService.addOrUpdate(userId,taskEntity);
    }


    @DeleteMapping("/{taskId}")
    public Result<?> delete(@PathVariable Long taskId){
        return taskService.delete(taskId);
    }

    @GetMapping("/detail/{taskId}")
    @ResponseBody
    public Result<TaskEntity> detail(@PathVariable Long taskId){
        return Result.success(taskService.getById(taskId));
    }

    @PutMapping("/status/{taskId}")
    public Result<?> setStatus(HttpServletRequest request, @PathVariable Long taskId, @RequestBody TaskEntity task){
        Long userId = RequestUtil.getUserId(request);
        return taskService.setStatus(userId, taskId, task.getStatus());
    }
}
