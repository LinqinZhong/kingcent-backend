package com.kingcent.campus.wx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.common.entity.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.kingcent.campus.wx.config.WxConfig.MINI_APP_ID;
import static com.kingcent.campus.wx.config.WxConfig.MINI_BASE_URL;

/**
 * 微信订阅消息发送服务
 * @author rainkyzhong
 * @date 2023/8/26 23:24
 */
@Service
@Slf4j
public class WxSubscribeMessageService {

    private static final String API = "/cgi-bin/message/subscribe/send";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WxTokenService wxTokenService;

    public Result<?> send(String templateId, String page, String openId, Map<String, Object> data, String programState, String lang){
        String accessToken = wxTokenService.getWxAccessToken(MINI_APP_ID);
        JSONObject body = new JSONObject();
        body.put("template_id",templateId);
        if(page != null) body.put("page",page);
        body.put("touser", openId);
        if(programState != null) body.put("miniprogram_state", programState);
        if(lang != null) body.put("lang", lang);
        body.put("data", data);
        String res = restTemplate.postForObject(
                MINI_BASE_URL + API + "?access_token=" + accessToken,
                body,
                String.class
        );
        JSONObject r = JSONObject.parseObject(res);
        if(r.containsKey("errmsg") && r.getString("errmsg").equals("ok")){
            return Result.success();
        }
        log.error("模板消息发送失败，url: {}, body: {}, res: {}",
                MINI_BASE_URL + API + "?access_token=" + accessToken,
                body,
                res
        );
        return Result.fail("发送失败").data(r);
    }
}
