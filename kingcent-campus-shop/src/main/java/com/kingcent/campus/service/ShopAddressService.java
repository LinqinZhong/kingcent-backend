package com.kingcent.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.shop.entity.AddressEntity;
import com.kingcent.campus.shop.entity.vo.AddressVo;
import com.kingcent.campus.shop.service.GroupPointService;
import com.kingcent.campus.shop.service.GroupService;
import com.kingcent.campus.shop.service.impl.AddressServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/9 10:41
 */
@Service
public class ShopAddressService extends AddressServiceImpl {
    @Autowired
    private GroupPointService groupPointService;

    @Autowired
    private GroupService groupService;


    @Override
    public List<AddressVo> getUserAddress(Long userId){
        List<AddressEntity> addressEntities = list(
                new QueryWrapper<AddressEntity>()
                        .eq("user_id", userId)
                        .orderByDesc("id")
        );
        List<AddressVo> res = new ArrayList<>();


        //提取信息
        Set<Long> groupIds = new HashSet<>();
        Set<Long> pointIds = new HashSet<>();
        for (AddressEntity addressEntity : addressEntities) {
            groupIds.add(addressEntity.getGroupId());
            pointIds.add(addressEntity.getPointId());
        }
        //获取配送点的名称
        Map<Long, String> groupNames = groupService.getGroupNames(groupIds);
        //获取门牌号的名称
        Map<Long, String> pointNames = groupPointService.getPointNames(pointIds);

        for (AddressEntity addressEntity : addressEntities) {
            AddressVo addressVo = new AddressVo();
            addressVo.setId(addressEntity.getId());
            addressVo.setMobile(addressEntity.getMobile());
            addressVo.setIsDefault(addressEntity.getIsDefault());
            addressVo.setGender(addressEntity.getGender());
            addressVo.setAddress(groupNames.get(addressEntity.getGroupId())+" "+pointNames.get(addressEntity.getPointId()));
            addressVo.setPointId(addressEntity.getPointId());
            addressVo.setSiteId(addressEntity.getSiteId());
            addressVo.setGroupId(addressEntity.getGroupId());
            addressVo.setName(addressEntity.getName());
            res.add(addressVo);
        }

        return res;
    }
}
