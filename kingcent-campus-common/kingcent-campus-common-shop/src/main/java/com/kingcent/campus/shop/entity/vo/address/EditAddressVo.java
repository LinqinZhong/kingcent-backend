package com.kingcent.campus.shop.entity.vo.address;

import lombok.Data;

@Data
public class EditAddressVo {
    //收货点id（即宿舍）
    private Long pointId;
    //收货人姓名
    private String name;
    //性别
    private Integer gender;
    //手机
    private String mobile;
}
