package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.WantKeywordEntity;
import com.kingcent.campus.shop.entity.vo.want.WantKeywordVo;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/17 6:58
 */
public interface WantKeywordService extends IService<WantKeywordEntity> {
    List<WantKeywordVo> getTopKeywords(Long groupId);
}
