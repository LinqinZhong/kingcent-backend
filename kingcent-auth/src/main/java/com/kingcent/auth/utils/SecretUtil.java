package com.kingcent.auth.utils;

import cn.hutool.crypto.digest.MD5;

public class SecretUtil {
    public static String get(Long uid, String code){
        return MD5.create().digestHex("userId="+uid+"&code="+code+"&time="+System.currentTimeMillis());
    }
}
