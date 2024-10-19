package com.kingcent.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.community.entity.PostDocumentEntity;
import com.kingcent.community.mapper.PostDocumentMapper;
import com.kingcent.community.service.PostDocumentService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/12/18 0:26
 */
@Service
public class PostDocumentServiceImpl extends ServiceImpl<PostDocumentMapper, PostDocumentEntity> implements PostDocumentService {
}
