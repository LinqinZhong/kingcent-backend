package com.kingcent.campus.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryVo {
    private Long id;
    private String name;
    private String thumbnail;
    private List<CategoryVo> children;
}
