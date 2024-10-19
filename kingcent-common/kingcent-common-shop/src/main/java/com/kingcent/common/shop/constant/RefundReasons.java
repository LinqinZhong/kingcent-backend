package com.kingcent.common.shop.constant;

import com.kingcent.common.shop.entity.RefundReasonEntity;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/24 0:09
 */
public class RefundReasons {
    /**
     * 注意：编号和索引的关系要是 编号 = 索引 + 1
     */
    public static final List<RefundReasonEntity> list = List.of(
            new RefundReasonEntity(1, "其它原因"),
            new RefundReasonEntity(2,"不想买了，与商家协商一致"),
            new RefundReasonEntity(3, "未按时配送上门"),
            new RefundReasonEntity(4, "商品与描述不符"),
            new RefundReasonEntity(5, "商品破损"),
            new RefundReasonEntity(6, "商家少发")
    );

    public static String getReasonValue(Integer id){
        return list.get((id - 1)).getValue();
    }
}
