package com.kingcent.campus.wx.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信发货信息
 * @author rainkyzhong
 * @date 2023/8/18 23:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxDeliveryInfoEntity {
    @JsonProperty("order_key")
    private WxDeliveryOrderKeyEntity orderKey;

    /**
     * 物流模式，发货方式枚举值：
     * 1、实体物流配送采用快递公司进行实体物流配送形式
     * 2、同城配送
     * 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式
     * 4、用户自提
     */
    @JsonProperty("logistics_type")
    private Long logisticsType;

    /**
     * 发货模式，发货模式枚举值：
     * 1、UNIFIED_DELIVERY（统一发货）
     * 2、SPLIT_DELIVERY（分拆发货）
     */
    @JsonProperty("delivery_mode")
    private Long deliveryMode;

    /**
     * 分拆发货模式时必填，
     * 用于标识分拆发货模式下【是否已全部发货完成】
     * 只有全部发货完成的情况下才会向用户推送发货完成通知。
     */
    @JsonProperty("is_all_delivered")
    private Boolean isAllDelivered;

    /**
     *物流信息列表，发货物流单列表
     * 支持统一发货（单个物流单）和分拆发货（多个物流单）两种模式，多重性: [1, 10]
     */
    @JsonProperty("shipping_list")
    private List<WxDeliveryShippingEntity> shippingList;

    /**
     * 上传时间，用于标识请求的先后顺序 示例值: `2022-12-15T13:29:35.120+08:00`
     */
    @JsonProperty("upload_time")
    private String uploadTime;

    @JsonProperty("payer")
    private WxDeliveryPayerEntity payer;
}
