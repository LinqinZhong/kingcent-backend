package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.plant.entity.TaskLandEntity;
import com.kingcent.plant.mapper.TaskLandMapper;
import com.kingcent.plant.service.TaskLandService;
import org.springframework.stereotype.Service;

@Service
public class TaskLandServiceImpl extends ServiceImpl<TaskLandMapper, TaskLandEntity> implements TaskLandService {
}
