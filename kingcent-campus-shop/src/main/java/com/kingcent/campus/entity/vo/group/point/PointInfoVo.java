package com.kingcent.campus.entity.vo.group.point;

import lombok.Data;

import java.util.List;

@Data
public class PointInfoVo {
    private List<EdgeVo> edges;
    private List<PointVo> points;
}
