package com.kingcent.campus.shop.entity.vo.address;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/8/20 8:32
 */
@AllArgsConstructor
@Data
public class AddressDetailsVo {
    //配送点ID
    private Long groupId;
    //收货点ID（即宿舍）
    private Long pointId;
    //收货人姓名
    private String name;
    //性别
    private Integer gender;
    //手机
    private String mobile;
}
