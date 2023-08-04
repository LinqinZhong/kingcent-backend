package com.kingcent.campus.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {
    /**
     * 获取用户id
     */
    public static Long getUserId(HttpServletRequest request){
        String userId = request.getHeader("uid");
        if(userId == null) throw new RuntimeException("用户身份缺失");
        return Long.valueOf(userId);
    }
}
