package com.kingcent.campus.wx.service;

/**
 * @author rainkyzhong
 * @date 2023/8/24 1:53
 */
public interface WxTokenStorageService {
    String getWxToken(String appId);
    void setWxToken(String appId, String token);
}
