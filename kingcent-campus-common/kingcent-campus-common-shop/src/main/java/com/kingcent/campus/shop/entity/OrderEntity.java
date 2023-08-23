package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 订单实体类
 */
@Data
@TableName("kc_shop_order")
public class OrderEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 交易号
     */
    private String tradeNo;

    /**
     * 配送点id
     */
    private Long groupId;

    /**
     * 点ID
     */
    private Long pointId;

    /**
     * 商店ID
     */
    private Long shopId;


    /**
     * 应付价格
     */
    private BigDecimal payPrice;

    /**
     * 商品总价
     */
    private BigDecimal goodsSumPrice;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 配送费用
     */
    private BigDecimal deliveryFee;

    /**
     * 配送时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 支付截至时间
     */
    private Long paymentDeadline;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机号
     */
    private String receiverMobile;

    /**
     * 收货人性别
     */
    private Integer receiverGender;

    /**
     * 是否已删除
     */
    @TableLogic
    private Boolean is_deleted;
}
