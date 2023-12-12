package com.kingcent.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.shop.entity.AddressEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.GroupEntity;
import com.kingcent.campus.shop.entity.GroupPointEntity;
import com.kingcent.campus.shop.entity.vo.address.AddressDetailsVo;
import com.kingcent.campus.shop.entity.vo.address.EditAddressVo;
import com.kingcent.campus.shop.entity.vo.group.point.FloorConsumePointVo;
import com.kingcent.campus.service.AddressService;
import com.kingcent.campus.service.GroupPointService;
import com.kingcent.campus.service.GroupService;
import com.kingcent.campus.service.SiteService;
import com.kingcent.campus.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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

//    @GetMapping("/nearby_group/{lng}/{lat}")
//    public Result<?> nearbyGroup(@PathVariable Double lng, @PathVariable Double lat){
//        return groupService.fetchNearbyGroups(lng, lat);
//    }

//    @GetMapping("/nearest_group/{lng}/{lat}")
//    public Result<?> nearestGroup(@PathVariable Double lng, @PathVariable Double lat){
//        return groupService.fetchNearestGroup(lng, lat);
//    }

//    @GetMapping("/nearby_site/{lng}/{lat}")
//    public Result<?> nearbySite(@PathVariable Double lng, @PathVariable Double lat){
//        return siteService.fetchNearestSite(lng, lat);
//    }

    @GetMapping("/max_floor_of_group/{groupId}")
    public Result<Integer> maxFloorOfGroup(@PathVariable Long groupId){
        Map<String, Object> res = groupPointService.getMap(
                new QueryWrapper<GroupPointEntity>()
                .eq("group_id", groupId)
                .select("MAX(floor) as floor")
                .last("limit 1")
        );
        if(res == null || !res.containsKey("floor"))
            return Result.fail("该楼栋没有可选的楼层，请联系管理员");
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
            return Result.fail("该楼栋没有可选的楼层，请联系管理员");
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
    public Result<?> createAddress(HttpServletRequest request, @RequestBody EditAddressVo vo){
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

        //获取楼栋信息
        GroupEntity group = groupService.getById(point.getGroupId());
        if(group == null){
            return Result.fail("楼栋不存在，请联系管理员");
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

    @DeleteMapping("/delete/{addressId}")
    public Result<?> deleteAddress(HttpServletRequest request, @PathVariable Long addressId){
        if (addressService.remove(new QueryWrapper<AddressEntity>()
                .eq("id", addressId)
                .eq("user_id", RequestUtil.getUserId(request))
        )) {
            return Result.success();
        }
        return Result.fail("收货地址不存在");
    }

    @PutMapping("/set_as_default/{addressId}")
    public Result<?> setAsDefault(HttpServletRequest request, @PathVariable Long addressId){
        if(addressService.setAsDefault(RequestUtil.getUserId(request), addressId)){
            return Result.success();
        }
        return Result.fail("收货地址不存在");
    }

    @PutMapping("/update/{addressId}")
    @Transactional
    public Result<?> updateAddress(HttpServletRequest request, @PathVariable Long addressId, @RequestBody EditAddressVo vo){
        AddressEntity address = new AddressEntity();
        address.setMobile(vo.getMobile());
        address.setGender(vo.getGender());
        address.setName(vo.getName());
        if(vo.getPointId() != null){
            //修改楼栋
            //获取门牌信息
            GroupPointEntity point = groupPointService.getById(vo.getPointId());
            if(point == null){
                return Result.fail("门牌号不存在");
            }

            //获取楼栋信息
            GroupEntity group = groupService.getById(point.getGroupId());
            if(group == null){
                return Result.fail("楼栋不存在，请联系管理员");
            }
            address.setPointId(point.getId());
            address.setGroupId(point.getGroupId());
        }


        if (addressService.update(
                address,
                new QueryWrapper<AddressEntity>()
                        .eq("user_id", RequestUtil.getUserId(request))
                        .eq("id", addressId)
        )) {
            return Result.success();
        }
        return Result.fail("收货地址不存在");
    }

    @GetMapping("/get_by_id/{id}")
    public Result<AddressDetailsVo> getById(HttpServletRequest request, @PathVariable Long id){
        AddressEntity address = addressService.getById(id);
        if(address == null || !address.getUserId().equals(RequestUtil.getUserId(request))){
            return Result.fail("收货地址不存在");
        }
        Result<Integer> maxFloor = maxFloorOfGroup(address.getGroupId());
        if(!maxFloor.getSuccess()){
            return Result.fail(maxFloor.getMessage());
        }

        GroupPointEntity point = groupPointService.getById(address.getPointId());

        Result<List<FloorConsumePointVo>> floorPoints = getFloorPoints(point.getGroupId(), point.getFloor());
        if(!floorPoints.getSuccess()){
            return Result.fail(floorPoints.getMessage());
        }

        return Result.success(new AddressDetailsVo(
                address.getGroupId(),
                address.getPointId(),
                address.getName(),
                address.getGender(),
                address.getMobile(),
                maxFloor.getData(),
                point.getFloor(),
                floorPoints.getData()
        ));
    }


}