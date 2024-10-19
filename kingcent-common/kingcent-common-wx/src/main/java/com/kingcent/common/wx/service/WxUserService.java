package com.kingcent.common.wx.service;

/**
 * 微信用户服务
 * 接入微信号绑定的用户实现此接口
 * @author rainkyzhong
 * @date 2023/8/20 3:20
 */
public interface WxUserService {
    Long getIdByWxOpenid(String openId);

    String getWxOpenid(Long userId);
}
