package com.kingcent.campus.shop.listener;

import java.util.Set;

/**
 * @author rainkyzhong
 * @date 2023/8/13 12:18
 */
public interface OrderListener {
    void onOverTime(Set<String> orderIds);
}
