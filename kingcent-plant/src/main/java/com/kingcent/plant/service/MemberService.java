package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.MemberEntity;
import com.kingcent.plant.entity.VarietyEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface MemberService extends IService<MemberEntity> {
    Page<MemberEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(MemberEntity memberEntity) throws KingcentSystemException;
    Result<?> delete(Long id);

    Result<MemberEntity> getByUserId(Long userId);
}
