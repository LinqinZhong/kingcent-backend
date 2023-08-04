package com.kingcent.campus.common.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class VoList<T> {
    private Integer total;
    private List<T> records;
}
