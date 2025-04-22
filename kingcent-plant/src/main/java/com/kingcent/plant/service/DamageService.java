package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.PesticideEntity;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface DamageService extends IService<DamageEntity> {
    Page<DamageEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(DamageEntity damageEntity);

    Result<?> delete(Long id);

    List<PesticideEntity> getTreatedBy(Long id);
}
