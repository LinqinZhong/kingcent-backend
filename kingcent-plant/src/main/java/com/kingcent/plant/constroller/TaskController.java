package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import com.kingcent.common.utils.NumberUtils;
import com.kingcent.plant.entity.TaskEntity;
import com.kingcent.plant.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
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
            HttpServletRequest request,
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize,
            @RequestParam(required = false)
            String memberIds,
            @RequestParam(required = false)
            String landIds,
            @RequestParam(required = false)
            Long planId,
            @RequestParam(required = false)
            String name,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            String type,
            @RequestParam(required = false)
            String startTimeFrom,
            @RequestParam(required = false)
            String startTimeEnd,
            @RequestParam(required = false)
            String endTimeFrom,
            @RequestParam(required = false)
            String endTimeEnd
    ) throws KingcentSystemException {
        Long userId = RequestUtil.getUserId(request);
        Page<TaskEntity> page = taskService.getPage(
                userId,
                pageNum,
                pageSize,
                planId,
                memberIds,
                landIds,
                name,
                NumberUtils.splitInt(status,",",true),
                NumberUtils.splitInt(type,",",true),
                startTimeFrom,
                startTimeEnd,
                endTimeFrom,
                endTimeEnd
        );
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
    public Result<TaskEntity> detail(HttpServletRequest request,@PathVariable Long taskId){
        Long userId = RequestUtil.getUserId(request);
        return Result.success(taskService.detail(userId, taskId));
    }

    @PutMapping("/status/{taskId}")
    public Result<?> setStatus(HttpServletRequest request, @PathVariable Long taskId, @RequestBody TaskEntity task){
        Long userId = RequestUtil.getUserId(request);
        return taskService.setStatus(userId, taskId, task.getStatus());
    }
}
