package com.kingcent.common.shop.entity.vo.address;

import com.kingcent.common.shop.entity.vo.group.point.FloorConsumePointVo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/20 8:32
 */
@AllArgsConstructor
@Data
public class AddressDetailsVo {
    //楼栋ID
    private Long groupId;
    //收货点ID（即宿舍）
    private Long pointId;
    //收货人姓名
    private String name;
    //性别
    private Integer gender;
    //手机
    private String mobile;
    //楼栋的最大楼层
    private Integer maxFloor;
    //地址所在楼层
    private Integer floor;
    //该楼层的所有房间号
    private List<FloorConsumePointVo> points;
}
