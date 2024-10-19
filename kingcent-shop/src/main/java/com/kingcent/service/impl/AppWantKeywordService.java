package com.kingcent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.service.WantKeywordService;
import com.kingcent.common.shop.entity.WantKeywordEntity;
import com.kingcent.common.shop.entity.vo.want.WantKeywordVo;
import com.kingcent.common.shop.mapper.WantKeywordMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/17 6:58
 */
@Service
public class AppWantKeywordService extends ServiceImpl<WantKeywordMapper, WantKeywordEntity> implements WantKeywordService {


    /**
     * 获取”想要“关键词
     */
    @Override
    public List<WantKeywordVo> getTopKeywords(Long groupId){
        List<WantKeywordVo> keywords = new ArrayList<>();
        List<WantKeywordEntity> list = list(new QueryWrapper<WantKeywordEntity>()
                .eq("group_id", groupId)
                .orderByDesc("count")
                .last("limit 10")
        );
        for (WantKeywordEntity entity : list) {
            keywords.add(new WantKeywordVo(
                    entity.getValue(),
                    entity.getCount()
            ));
        }
        return keywords;
    }
}
