package com.kingcent.campus.service.impl;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.service.WxPayService;
import com.kingcent.campus.shop.constant.PayType;
import com.kingcent.campus.shop.entity.vo.payment.WxPaymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.kingcent.campus.shop.util.WxPayUtil.*;

/**
 * @author rainkyzhong
 * @date 2023/8/18 13:11
 * @warning 支付敏感信息，注意保密，禁止外传，侵权必究
 */
@Slf4j
@Service
public class AppWxPayService implements WxPayService {

    /**
     * 小程序appId
     */
    @Value("${third.wechat.appid}")
    private String WX_APP_ID;

    /**
     * 密钥
     */
    @Value("${third.wechat.key}")
    private String KEY;

    /**
     * 商户号
     */
    @Value("${third.wechat.mchid}")
    private String WX_MCH_ID;

    /**
     * 支付回调地址
     */
    @Value("https://a.intapter.cn/shop/order/wx_payment_notify_2023_8")
    private String PAYMENT_CALL_BACK_URL;

    /**
     * 统一下单接口
     */
    private final static String WX_PAYMENT_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //时间格式模板
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppUserService userService;

    @Autowired
    @Lazy
    private OrderService orderService;

    /**
     * 发起微信支付接口
     *
     * @param openId          用户openId
     * @param outTradeNo      订单outTradeNo（多个orderNo可以对应一个outTradeNo，实现多个订单同时支付）
     * @param body            商品描述
     * @param payPrice        支付价格
     * @param ipAddress       ip地址
     * @param orderCreateTime 订单创建时间
     *)
     */
    @Override
    public WxPaymentInfo requestToPay(
            String openId,
            String outTradeNo,
            String body,
            Long payPrice,
            String ipAddress,
            LocalDateTime orderCreateTime
    )  {
        String nonceStr = createNonce();
        SortedMap<String, Object> data = new TreeMap<>();
        data.put("appid", WX_APP_ID);
        data.put("mch_id", WX_MCH_ID);
        //data.put("device_info","");   //设备号
        data.put("nonce_str", nonceStr);
        data.put("body", body);
        data.put("attach", "测试");   //附加信息
        data.put("out_trade_no", outTradeNo);  //订单编号
        data.put("total_fee", payPrice);  //总价
        data.put("spbill_create_ip", ipAddress);    //终端IP
        data.put("time_start",orderCreateTime.format(dateTimeFormatter));
        data.put("notify_url", PAYMENT_CALL_BACK_URL);
        data.put("trade_type", "JSAPI");    //支付类型（JSAPI小程序支付）
        data.put("openid", openId);     //微信小程序用户openId
        //计算签名
        String sign = getSignByMd5(data, KEY);
        data.put("sign", sign);
        //转为XML格式
        String xmlData = XmlUtil.mapToXmlStr(data, true);
        log.info("请求数据->{}", xmlData);
        //发送请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> formEntity = new HttpEntity<>(xmlData, headers);
        String res = restTemplate.postForObject(WX_PAYMENT_API, formEntity, String.class);
        try {
            Map<String, Object> map = XmlUtil.xmlToMap(res);
            log.info("响应数据->{}", map);
            if(map.containsKey("result_code") && "SUCCESS".equals(map.get("result_code"))){
                //再次签名
                String nonceStr2 = createNonce();
                String packageStr = "prepay_id="+map.get("prepay_id");
                long timestamp = System.currentTimeMillis()/1000;
                String resign = paymentResign((String) map.get("appid"),nonceStr2,packageStr,timestamp,KEY);
                return new WxPaymentInfo(
                        (String) map.get("app_id"),
                        timestamp+"",
                        nonceStr2,
                        packageStr,
                        "MD5",
                        resign
                );
            }else{
                log.error("订单编号->{}\n接口返回结果->{}",outTradeNo,res);
                throw new RuntimeException("微信支付接口调用失败1");
            }
        }catch (Exception e){
            log.error("订单编号->{}\n接口返回结果->{}",outTradeNo,res);
            e.printStackTrace();
            throw new RuntimeException("微信支付接口调用失败");
        }
    }


    /**
     * 微信支付回调接口
     * @param xmlData 回调数据
     * @return 验证信息
     * <xml>
     *   <return_code><![CDATA[SUCCESS]]></return_code>
     *   <return_msg><![CDATA[OK]]></return_msg>
     * </xml>
     */
    @Override
    public String notify(String xmlData){
        log.info("收到支付回调信息，{}", xmlData);
        Map<String, Object> data = XmlUtil.xmlToMap(xmlData);
        log.info("Map格式，{}", data);
        JSONObject res = new JSONObject();
        if(!data.containsKey("return_code")
                || !"SUCCESS".equals(data.get("return_code"))
                || !data.containsKey("sign")
                || !data.containsKey("result_code")
                || !"SUCCESS".equals(data.get("result_code"))
        ){
            res.put("return_code", "FAIL");
            return XmlUtil.mapToXmlStr(res, true);
        }
        //验签
        String sign = (String) data.get("sign");
        String signType = (String) data.getOrDefault("signType","MD5");
        data.remove("sign");    //移除sign
        String realSign = signType.equals("MD5") ? getSignByMd5(new TreeMap<>(data), KEY) : getSignBySha256(new TreeMap<>(data), KEY);
        if(!realSign.equals(sign)){
            log.info("签名错误，已拦截");
            res.put("return_code", "FAIL");
            res.put("return_msg", "签名错误");
            return XmlUtil.mapToXmlStr(res, true);
        }
        //获取订单
        try{
            String openId = (String) data.get("openid");
            Integer totalFee = Integer.parseInt((String) data.get("total_fee"));
            String tradeNo = (String) data.get("transaction_id");
            String outTradeNo = (String) data.get("out_trade_no");
            LocalDateTime payTime = LocalDateTime.parse(data.get("time_end").toString(), dateTimeFormatter);

            //获取用户Id
            Long userId = userService.getIdByOpenid(openId);
            if(userId == -1L){
                log.info("用户不存在[{}]，已拦截", openId);
                res.put("return_code", "FAIL");
                res.put("return_msg", "用户不存在");
                return XmlUtil.mapToXmlStr(res, true);
            }

            //更新订单
            Result<?> result = orderService.onPayed(userId, outTradeNo, tradeNo, totalFee, payTime, PayType.WX_PAY);
            if(result.getSuccess()){
                log.info("订单更新成功");
                res.put("return_code", "SUCCESS");
                res.put("return_msg", "OK");
                return XmlUtil.mapToXmlStr(res, true);
            }
            log.info("订单更新失败，{}", result.getMessage());
            res.put("return_code", "FAIL");
            res.put("return_msg", result.getMessage());
            return XmlUtil.mapToXmlStr(res, true);

        }catch (Exception e){
            log.error("支付回调出现异常，{}", e.getMessage());
            e.printStackTrace();
            res.put("return_code", "FAIL");
            res.put("return_msg", e.getMessage());
            return XmlUtil.mapToXmlStr(res, true);
        }
    }
}