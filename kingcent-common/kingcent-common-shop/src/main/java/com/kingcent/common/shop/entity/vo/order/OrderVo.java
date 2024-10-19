package com.kingcent.common.shop.entity.vo.order;

import com.kingcent.common.entity.vo.VoList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
/**
 * @author rainkyzhong
 * @date 2023/8/11 7:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo {
    private LocalDateTime currentTime;
    private VoList<OrderStoreVo> orders;
}
