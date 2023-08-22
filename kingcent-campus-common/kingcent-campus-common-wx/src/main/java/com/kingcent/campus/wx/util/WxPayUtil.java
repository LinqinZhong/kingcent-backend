package com.kingcent.campus.wx.util;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.kingcent.campus.wx.config.WxConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;
import java.security.*;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:20
 */
@Slf4j
public class WxPayUtil {

    /**
     * 拼接V3接口鉴权的请求头
     */
    public static String createV3Authorization(String nonce, long timestamp, String sign){
        String authInfo = "mchid=\"" + WxConfig.MCH_ID + "\","
                + "nonce_str=\"" + nonce + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + WxConfig.CERTIFICATE_SERIAL_NO + "\","
                + "signature=\"" + sign + "\"";
        return "WECHATPAY2-SHA256-RSA2048 " + authInfo;
    }

    public static String getSign(String method, String url, long timestamp, String nonce, SortedMap<String, Object> data, PrivateKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String originalText = method+"\n"
                +url+"\n"
                +timestamp+"\n"
                +nonce+"\n"
                +(method.equals("GET") ? "" : data == null ? "" : JSONObject.toJSONString(data))+"\n";
        log.info("originalText: {}\n", originalText);
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(key);
        sign.update(originalText.getBytes());
        return Base64.getEncoder().encodeToString(sign.sign());
    }

    /**
     * 生成ASCII码排序后的键值对
     * @param data 参数
     * @return 键值对
     */
    public static String toKeyValueString(SortedMap<String, Object> data){
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
    public static String getSignBySha256(String text, String key){
        Mac mac = HmacUtils.getInitializedMac(
                HmacAlgorithms.HMAC_SHA_256,
                key.getBytes()
        );
        return bytesToHex(mac.doFinal(text.getBytes())).toUpperCase();
    }

    /**
     * SHA256计算签名
     */
    public static String getSignBySha256(SortedMap<String, Object> data, String key){
        String originalString = toKeyValueString(data);
        log.info("原始加密信息->{}" ,originalString);
        String sign = getSignBySha256(originalString,key);
        log.info("SHA256签名->{}", sign);
        return sign;
    }

    /**
     * MD5计算签名
     * @param data 参数
     * @return 签名
     */
    public static String getSignByMd5(SortedMap<String, Object> data, String key){
        String originalString = toKeyValueString(data);
        log.info("原始加密信息->{}" ,originalString);
        String sign;
        try {
            sign = MD5Utils.md5Hex((originalString+"&key="+key).getBytes()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        log.info("MD5签名->{}", sign);
        return sign;
    }

    /**
     * 微信支付再签名
     */
    public static String paymentResign(String appid, String nonce, String packageStr, long timestamp, String key){
        try {
            return MD5Utils.md5Hex(
                    ("appId="+appid
                            +"&nonceStr="+nonce
                            +"&package="+packageStr
                            +"&signType=MD5"
                            +"&timeStamp="+timestamp
                            +"&key="+key
                    ).getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(byte[] hash) {
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
    public static String createNonce(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 20; i++){
            sb.append((char) random.nextInt(65,122));
        }
        return sb.toString();
    }
}
