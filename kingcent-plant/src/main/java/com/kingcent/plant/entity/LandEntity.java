package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@Data
@TableName("kc_agrc_land")
public class LandEntity {
    private Long id;
    private String name;
    private String pic;
    private String no;
    private BigDecimal area;
    private BigDecimal usedArea;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
