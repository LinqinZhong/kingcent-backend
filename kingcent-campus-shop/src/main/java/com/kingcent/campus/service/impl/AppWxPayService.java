package com.kingcent.campus.service.impl;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.service.WxPayService;
import com.kingcent.campus.shop.constant.PayType;
import com.kingcent.campus.shop.entity.vo.payment.WxPaymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/18 13:11
 * @warning 支付敏感信息，禁止外传，侵权必究
 */
@Slf4j
@Service
public class AppWxPayService implements WxPayService {

    /**
     * 小程序appId
     */
    @Value("wx78ac22e61f72cba5")
    private String WX_APP_ID;

    /**
     * 商户号
     */
    @Value("1650981448")
    private String WX_MCH_ID;

    /**
     * 支付回调地址
     */
    @Value("https://a.intapter.cn/shop/order/wx_payment_notify_2023_8")
    private String PAYMENT_CALL_BACK_URL;

    /**
     * 退款回调地址
     */
    @Value("https://a.intapter.cn/shop/order/wx_refund_notify_2023_8")
    private String REFUND_CALL_BACK_URL;

    /**
     * 统一下单接口
     */
    private final static String WX_PAYMENT_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    /**
     * 退款接口
     */
    private final static String WX_REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";


    /**
     * 密钥
     */
    @Value("9620129cf275252ec9b3e9cd42e5bf56")
    private String KEY;

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
     * 生成ASCII码排序后的键值对
     * @param data 参数
     * @return 键值对
     */
    private String toKeyValueString(SortedMap<String, Object> data){
        Set<Map.Entry<String, Object>> entries = data.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        List<String> values = Lists.newArrayList();
        while(iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            String k = String.valueOf(entry.getKey());
            String v = String.valueOf(entry.getValue());
            if (StringUtils.isNotEmpty(v) && entry.getValue() !=null && !"sign".equals(k) && !"key".equals(k)) {
                values.add(k + "=" + v);
            }
        }
        return String.join("&",values);
    }

    /**
     * SHA256计算签名
     */
    private String getSignBySha256(String text){
        Mac mac = HmacUtils.getInitializedMac(
                HmacAlgorithms.HMAC_SHA_256,
                KEY.getBytes()
        );
        return bytesToHex(mac.doFinal(text.getBytes())).toUpperCase();
    }

    /**
     * SHA256计算签名
     */
    public String getSignBySha256(SortedMap<String, Object> data){
        String originalString = toKeyValueString(data);
        log.info("原始加密信息->{}" ,originalString);
        String sign = getSignBySha256(originalString);
        log.info("SHA256签名->{}", sign);
        return sign;
    }

    /**
     * MD5计算签名
     * @param data 参数
     * @return 签名
     */
    public String getSignByMd5(SortedMap<String, Object> data){
        String originalString = toKeyValueString(data);
        log.info("原始加密信息->{}" ,originalString);
        String sign = null;
        try {
            sign = MD5Utils.md5Hex((originalString+"&key="+KEY).getBytes()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        log.info("MD5签名->{}", sign);
        return sign;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 生成随机字符串
     * @return 随机字符串
     */
    private static String createNonce(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 20; i++){
            sb.append((char) random.nextInt(65,122));
        }
        return sb.toString();
    }

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
        String sign = getSignByMd5(data);
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
                String resign = MD5Utils.md5Hex(
                        ("appId="+map.get("appid")
                                +"&nonceStr="+nonceStr2
                                +"&package="+packageStr
                                +"&signType=MD5"
                                +"&timeStamp="+timestamp
                                +"&key="+KEY
                        ).getBytes()
                );
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
        String realSign = signType.equals("MD5") ? getSignByMd5(new TreeMap<>(data)) : getSignBySha256(new TreeMap<>(data));
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

    /**
     * 发起退款请求
     * @param openId 微信openId
     * @param orderIds 订单ID集合
     * @param tradeNo   支付编号
     * @param reason 退款原因
     */
    public Result<?> requestToRefund(String openId,String tradeNo, List<Long> orderIds, String reason){
        SortedMap<String, Object> data = new TreeMap<>();
        data.put("transaction_id" ,tradeNo);
        data.put("out_refund_no", "");
        data.put("reason",reason);
        data.put("notify_url", REFUND_CALL_BACK_URL);

        SortedMap<String, Object> amount = new TreeMap<>();
        amount.put("refund", ); //退款金额
        amount.put("integer", ) //原订单金额
        amount.put("currency" ,"CNY");   //退款币种
        data.put("amount",amount);

        List<SortedMap<String, Object>> goodsDetails = new ArrayList<>();
        SortedMap<String, Object> goodsDetail = new TreeMap<>();
        goodsDetails.add(goodsDetail);
        goodsDetail.put("merchant_goods_id", ); //商品编号
        goodsDetail.put("goods_name", );    //商品名称
        goodsDetail.put("unit_price", );    //商品单价
        goodsDetail.put("refund_amount", ); //商品退款金额
        goodsDetail.put("refund_quantity", );   //退货数量
        data.put("goods_detail", goodsDetails);

        //请求头部
        String nonce = createNonce();
        long timestamp = System.currentTimeMillis()/1000;

        String sign = getSign("POST", "/secapi/pay/refund",nonce,data);
        String authInfo = "mchid=\"" + WX_MCH_ID + "\","
                + "nonce_str=\"" + nonce + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + yourCertificateSerialNo + "\","
                + "signature=\"" + sign + "\"";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "WECHATPAY2-SHA256-RSA2048 "+authInfo);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

    }

    private String getSign(String method, String url, String nonce, SortedMap<String, Object> data){
        String originalText = method+"\n"
                +url+"\n"
                +nonce+"\n"
                +(method.equals("GET") ? "" :JSONObject.toJSONString(data))+"\n";
        log.info("originalText: {}\n", originalText);
        String sign =  getSignBySha256(originalText);
        log.info("sign, {}", sign);
        return sign;
    }
}