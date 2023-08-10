package com.kingcent.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.shop.entity.AddressEntity;
import com.kingcent.campus.shop.entity.GroupEntity;
import com.kingcent.campus.shop.entity.GroupPointEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.CreateAddressVo;
import com.kingcent.campus.shop.entity.vo.group.point.FloorConsumePointVo;
import com.kingcent.campus.shop.service.AddressService;
import com.kingcent.campus.shop.service.GroupPointService;
import com.kingcent.campus.shop.service.GroupService;
import com.kingcent.campus.shop.service.SiteService;
import com.kingcent.campus.shop.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupPointService groupPointService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private SiteService siteService;

    @GetMapping("/nearby_group/{lng}/{lat}")
    public Result<?> nearbyGroup(@PathVariable Double lng, @PathVariable Double lat){
        return groupService.fetchNearbyGroups(lng, lat);
    }

    @GetMapping("/nearest_group/{lng}/{lat}")
    public Result<?> nearestGroup(@PathVariable Double lng, @PathVariable Double lat){
        return groupService.fetchNearestGroup(lng, lat);
    }

    @GetMapping("/nearby_site/{lng}/{lat}")
    public Result<?> nearbySite(@PathVariable Double lng, @PathVariable Double lat){
        return siteService.fetchNearestSite(lng, lat);
    }

    @GetMapping("/max_floor_of_group/{groupId}")
    public Result<Integer> maxFloorOfGroup(@PathVariable Long groupId){
        Map<String, Object> res = groupPointService.getMap(
                new QueryWrapper<GroupPointEntity>()
                .eq("group_id", groupId)
                .select("MAX(floor) as floor")
                .last("limit 1")
        );
        if(res == null || !res.containsKey("floor"))
            return Result.fail("该配送点没有可选的楼层，请联系管理员");
        return Result.success((Integer)(res.get("floor")));
    }

    @GetMapping("/get_floor_points/{groupId}/{floor}")
    public Result<List<FloorConsumePointVo>> getFloorPoints(@PathVariable Long groupId, @PathVariable("floor") Integer floor){
        List<GroupPointEntity> pointEntities = groupPointService.list(
                new QueryWrapper<GroupPointEntity>()
                        .eq("group_id", groupId)
                        .eq("floor", floor)
                        .eq("type", 0)
                        .select("id, name")
        );
        if(pointEntities.size() == 0)
            return Result.fail("该配送点没有可选的楼层，请联系管理员");
        List<FloorConsumePointVo> points = new ArrayList<>();
        for (GroupPointEntity pointEntity : pointEntities) {
            FloorConsumePointVo point = new FloorConsumePointVo();
            point.setId(pointEntity.getId());
            point.setName(pointEntity.getName());
            points.add(point);
        }
        return Result.success(points);
    }

    @PostMapping("/create_address")
    public Result<?> createAddress(HttpServletRequest request, @RequestBody CreateAddressVo vo){
        if (vo == null) return Result.fail("参数格式错误");
        AddressEntity addressEntity = new AddressEntity();
        if (vo.getName() == null || vo.getMobile() == null || vo.getGender() == null)
            return Result.fail("收货人信息");
        if (vo.getPointId() == null)
            return Result.fail("缺少收货地址");

        //获取门牌信息
        GroupPointEntity point = groupPointService.getById(vo.getPointId());
        if(point == null){
            return Result.fail("门牌号不存在");
        }

        //获取配送点信息
        GroupEntity group = groupService.getById(point.getGroupId());
        if(group == null){
            return Result.fail("配送点不存在，请联系管理员");
        }
        addressEntity.setUserId(RequestUtil.getUserId(request));
        addressEntity.setSiteId(group.getSiteId());
        addressEntity.setGroupId(group.getId());
        addressEntity.setPointId(vo.getPointId());
        addressEntity.setName(vo.getName());
        addressEntity.setMobile(vo.getMobile());
        addressEntity.setGender(vo.getGender());

        if(addressService.save(addressEntity)) return Result.success();
        return Result.fail("创建失败");
    }

    @GetMapping("/get_address")
    public Result<?> createAddress(HttpServletRequest request){
        return Result.success(addressService.getUserAddress(RequestUtil.getUserId(request)));
    }

}