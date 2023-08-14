package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.ShopEntity;
import com.kingcent.campus.shop.entity.vo.shop.ShopNameVo;
import com.kingcent.campus.shop.mapper.ShopMapper;
import com.kingcent.campus.shop.service.ShopService;
import com.kingcent.campus.shop.util.BeanCopyUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2023/8/14 18:45
 */
@Service
public class AdminShopService extends ServiceImpl<ShopMapper, ShopEntity> implements ShopService {
    @Override
    public Map<Long, String> shopNamesMap(Collection<Long> shopIds) {
        return null;
    }

    /**
     * 获取店铺名称列表（用于下拉选择店铺）
     * @param keywords 关键词（可以是店铺id，也可以是店铺名称）
     * @param page 页数
     */
    @Override
    public VoList<ShopNameVo> getShopNames(String keywords, Integer page){
        Long id = null;
        //尝试转为数字类型
        try {
            id = Long.valueOf(keywords);
        }catch (Exception ignored){}
        QueryWrapper<ShopEntity> wrapper = new QueryWrapper<>();
        if(id != null) wrapper.eq("id", id);
        if(keywords != null) wrapper.like("name", keywords);
        Page<ShopEntity> pager = new Page<>(page, 10, true);
        Page<ShopEntity> res = page(pager, wrapper);
        List<ShopNameVo> shopNameVoList = BeanCopyUtils.copyBeanList(res.getRecords(), ShopNameVo.class);
        return new VoList<>((int) res.getTotal(), shopNameVoList);
    }

    @Override
    public boolean exists(Long shopId) {
        return count(new QueryWrapper<ShopEntity>().eq("id", shopId)) > 0;
    }
}
