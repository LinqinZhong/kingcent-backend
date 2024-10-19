package com.kingcent.common.shop.entity.vo.goods;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsTableVo {
    private Long id;
    private String name;
    private Long shopId;
    private String shopName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String thumbnail;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isSale;
    private Integer sales;
}
