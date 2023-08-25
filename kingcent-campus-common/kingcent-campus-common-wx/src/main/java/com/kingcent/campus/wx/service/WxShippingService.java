package com.kingcent.campus.wx.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.wx.config.WxConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.kingcent.campus.wx.config.WxConfig.*;

/**
 * @author rainkyzhong
 * @date 2023/8/24 1:45
 */
@Service
@Slf4j
public class WxShippingService {

    @Autowired(required = false)
    private WxTokenStorageService tokenStorageService;

    @Autowired
    private WxTokenService tokenService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WxUserService userService;

    private final static String API = "/wxa/sec/order/upload_shipping_info";

    public Result<?> upload(Long userId, String orderNo, String goodsDescription){
        String accessToken = null;
        if(tokenStorageService != null){
            accessToken = tokenStorageService.getWxToken(WxConfig.MINI_APP_ID);
        }
        //token为空，重新加载
        if(accessToken == null){
            accessToken = tokenService.fetchWxAccessToken(WxConfig.MINI_APP_ID, WxConfig.MINI_SECRET);
        }
        if(accessToken == null){
            log.error("获取accessToken失败");
            return Result.fail("服务异常，请稍后重试");
        }
        JSONObject data = new JSONObject();

        JSONObject orderKey = new JSONObject();
        orderKey.put("order_number_type", 1);   //使用mchid和out_trade_no
        orderKey.put("mchid",MCH_ID);
        orderKey.put("out_trade_no", orderNo);

        JSONArray shippingList = new JSONArray();
        JSONObject shipping = new JSONObject();
        shipping.put("item_desc", goodsDescription);
        shippingList.add(shipping);

        JSONObject payer = new JSONObject();
        payer.put("openid", userService.getWxOpenid(userId));

        data.put("logistics_type", 2);  //发货方式，2是同城配送
        data.put("delivery_mode", "UNIFIED_DELIVERY");    //分拆发货
        data.put("order_key", orderKey);
        data.put("shipping_list", shippingList);
        data.put("upload_time", (LocalDateTime.now().atOffset(ZoneOffset.ofHours(8)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        data.put("payer", payer);

        Map<String, Object> map = restTemplate.postForObject(MINI_BASE_URL + API + "?access_token=" + accessToken, data, Map.class);

        if(map != null && map.containsKey("errmsg")){
            String msg  = (String) map.get("errmsg");
            if (msg.equals("ok")){
                return Result.success();
            }
            log.error("发货信息上传失败, 报文: {}， 响应: {}",data, map);
            return Result.fail(msg);
        }
        log.error("发货信息上传失败, 报文: {}， 响应: {}",data, map);
        return Result.fail("服务异常");

    }
}
