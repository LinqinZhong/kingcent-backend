package com.kingcent.campus.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.community.dto.CreateDocumentDto;
import com.kingcent.campus.community.entity.DocumentEntity;
import com.kingcent.campus.community.vo.DocumentVo;

/**
 * @author rainkyzhong
 * @date 2023/12/12 14:39
 */
public interface DocumentService extends IService<DocumentEntity> {
    Result<VoList<DocumentVo>> list(Integer page, Integer pageSize);

    Result<?> create(Long userId, CreateDocumentDto dto);
}
