package com.kingcent.common.shop.entity.vo;

import lombok.Data;

/**
 * 我的页面信息
 * @author rainkyzhong
 * @date 2023/8/14 17:33
 */
@Data
public class UserCenterVo {
    private String nickname;
    private String avatarUrl;
    private Integer countOrderToPay;
    private Integer countOrderToDelivery;
    private Integer countOrderToReview;
    private Integer countOrderAfterSales;
}
