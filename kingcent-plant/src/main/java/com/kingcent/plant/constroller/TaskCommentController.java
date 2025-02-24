package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import com.kingcent.plant.entity.TaskCommentEntity;
import com.kingcent.plant.entity.TaskCommentEntity;
import com.kingcent.plant.service.TaskCommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/taskComment")
public class TaskCommentController {
    @Autowired
    private TaskCommentService taskCommentService;

    @GetMapping("/{taskId}/{pageNum}/{pageSize}")
    public Result<Page<TaskCommentEntity>> list(
            @PathVariable
            Long taskId,
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ) throws KingcentSystemException {
        Page<TaskCommentEntity> page = taskCommentService.getPage(taskId,pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> add(HttpServletRequest request, @RequestBody TaskCommentEntity taskCommentEntity){
        Long userId = RequestUtil.getUserId(request);
        return taskCommentService.addOrUpdate(userId, taskCommentEntity);
    }


    @DeleteMapping("/{taskCommentId}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long taskCommentId){
        Long userId = RequestUtil.getUserId(request);
        return taskCommentService.delete(userId,taskCommentId);
    }
}
