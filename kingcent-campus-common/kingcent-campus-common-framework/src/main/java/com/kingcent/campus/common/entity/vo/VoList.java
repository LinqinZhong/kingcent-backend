package com.kingcent.campus.common.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoList<T> {
    private Integer total;
    private List<T> records;
}
