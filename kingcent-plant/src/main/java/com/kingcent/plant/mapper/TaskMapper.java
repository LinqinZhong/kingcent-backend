package com.kingcent.plant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.plant.entity.PlanEntity;
import com.kingcent.plant.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {
}
