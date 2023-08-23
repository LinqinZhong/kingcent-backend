package com.kingcent.campus.wx.service;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.wx.config.WxConfig;
import com.kingcent.campus.wx.entity.vo.WxPaymentInfoVo;
import com.kingcent.campus.wx.util.WxPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.kingcent.campus.wx.config.WxConfig.MCH_BASE_URL;


/**
 * @author rainkyzhong
 * @date 2023/8/18 13:11
 */
@Slf4j
public class WxPayService {

    @Autowired
    private WxCertificateService certificateService;

    /**
     * 时间格式模板
     */
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 统一下单接口
     */
    private final static String WX_PAYMENT_API = "/pay/unifiedorder";

    /**
     * 查单接口
     */
    private final static String CHECK_API = "/v3/pay/transactions/out-trade-no/";


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Lazy
    private WxUserService userService;

    @Autowired
    @Lazy
    private WxOrderService orderService;

    /**
     * 查询订单
     * trade_state 支付状态
     * SUCCESS：支付成功
     * REFUND：转入退款
     * NOTPAY：未支付
     * CLOSED：已关闭
     * REVOKED：已撤销（仅付款码支付会返回）
     * USERPAYING：用户支付中（仅付款码支付会返回）
     * PAYERROR：支付失败（仅付款码支付会返回）
     */
    public JSONObject checkOrder(String orderNo){
        String api = CHECK_API+orderNo+"?mchid="+WxConfig.MCH_ID;
        long timestamp = System.currentTimeMillis()/1000;
        String nonce = WxPayUtil.createNonce();
        //获取证书私钥
        PrivateKey key = certificateService.getPrivateKey();
        try {
            String sign = WxPayUtil.getSign("GET",api,timestamp,nonce,null, key);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", WxPayUtil.createV3Authorization(nonce, timestamp, sign));
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> exchange = restTemplate.exchange(MCH_BASE_URL + api, HttpMethod.GET, request, String.class);
            if(exchange.getStatusCode().is2xxSuccessful()){
                return JSONObject.parseObject(exchange.getBody());
            }
            return null;
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 发起微信支付接口
     *
     * @param openId          用户openId
     * @param orderNo      订单outTradeNo（多个orderNo可以对应一个outTradeNo，实现多个订单同时支付）
     * @param body            商品描述
     * @param payPrice        支付价格
     * @param ipAddress       ip地址
     * @param orderCreateTime 订单创建时间
     * @param expireTime 订单结束时间
     */
    public WxPaymentInfoVo requestToPay(
            String openId,
            String orderNo,
            String body,
            Long payPrice,
            String ipAddress,
            LocalDateTime orderCreateTime,
            LocalDateTime expireTime
    )  {
        String nonceStr = WxPayUtil.createNonce();
        SortedMap<String, Object> data = new TreeMap<>();
        data.put("appid", WxConfig.MINI_APP_ID);
        data.put("mch_id", WxConfig.MCH_ID);
        data.put("time_expire", expireTime.format(dateTimeFormatter));
        //data.put("device_info","");   //设备号
        data.put("nonce_str", nonceStr);
        data.put("body", body);
        data.put("attach", "测试");   //附加信息
        data.put("out_trade_no", orderNo);  //订单编号
        data.put("total_fee", payPrice);  //总价
        data.put("spbill_create_ip", ipAddress);    //终端IP
        data.put("time_start",orderCreateTime.format(dateTimeFormatter));
        data.put("notify_url", WxConfig.PAYMENT_CALL_BACK_URL);
        data.put("trade_type", "JSAPI");    //支付类型（JSAPI小程序支付）
        data.put("openid", openId);     //微信小程序用户openId
        //计算签名
        String sign = WxPayUtil.getSignByMd5(data, WxConfig.API_V2_KEY);
        data.put("sign", sign);
        //转为XML格式
        String xmlData = XmlUtil.mapToXmlStr(data, true);
        log.info("请求数据->{}", xmlData);
        //发送请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> formEntity = new HttpEntity<>(xmlData, headers);
        String res = restTemplate.postForObject(MCH_BASE_URL +WX_PAYMENT_API, formEntity, String.class);
        try {
            Map<String, Object> map = XmlUtil.xmlToMap(res);
            log.info("响应数据->{}", map);
            if(map.containsKey("result_code") && "SUCCESS".equals(map.get("result_code"))){
                //再次签名
                String nonceStr2 = WxPayUtil.createNonce();
                String packageStr = "prepay_id="+map.get("prepay_id");
                long timestamp = System.currentTimeMillis()/1000;
                String resign = WxPayUtil.paymentResign((String) map.get("appid"),nonceStr2,packageStr,timestamp,WxConfig.API_V2_KEY);
                return new WxPaymentInfoVo(
                        (String) map.get("app_id"),
                        timestamp+"",
                        nonceStr2,
                        packageStr,
                        "MD5",
                        resign
                );
            }else{
                log.error("订单编号->{}\n接口返回结果->{}",orderNo,res);
                throw new RuntimeException("微信支付接口调用失败1");
            }
        }catch (Exception e){
            log.error("订单编号->{}\n接口返回结果->{}",orderNo,res);
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
        String realSign = signType.equals("MD5") ? WxPayUtil.getSignByMd5(new TreeMap<>(data), WxConfig.API_V2_KEY) : WxPayUtil.getSignBySha256(new TreeMap<>(data), WxConfig.API_V2_KEY);
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
            Long userId = userService.getIdByWxOpenid(openId);
            if(userId == -1L){
                log.info("用户不存在[{}]，已拦截", openId);
                res.put("return_code", "FAIL");
                res.put("return_msg", "用户不存在");
                return XmlUtil.mapToXmlStr(res, true);
            }

            //更新订单
            Result<?> result = orderService.onWxPayed(userId, outTradeNo, tradeNo, totalFee, payTime);
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