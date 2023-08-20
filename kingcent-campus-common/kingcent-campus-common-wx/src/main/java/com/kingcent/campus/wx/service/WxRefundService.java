package com.kingcent.campus.wx.service;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.wx.config.WxConfig;
import com.kingcent.campus.wx.entity.WxOrderGoodsEntity;
import com.kingcent.campus.wx.util.AesUtil;
import com.kingcent.campus.wx.util.WxPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:22
 */
@Slf4j
public class WxRefundService {

    @Autowired
    private WxCertificateService certificateService;

    @Autowired
    private RestTemplate restTemplateForWxService;

    @Autowired
    @Lazy
    private WxOrderService orderService;

    /**
     * 退款接口
     */
    private final static String WX_REFUND_API = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";


    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .optionalStart()
            .appendOffset("+HH:mm", "+00:00")
            .optionalEnd()
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .toFormatter();


    /**
     * 发起退款请求
     */
    public JSONObject requestToRefund(
            String tradeNo,
            String outRefundNo,
            long price,
            long total,
            List<WxOrderGoodsEntity> goodsList,
            String reason
    )  {
        SortedMap<String, Object> data = new TreeMap<>();
        data.put("transaction_id" ,tradeNo);
        data.put("out_refund_no", outRefundNo);
        data.put("reason",reason);
        data.put("notify_url", WxConfig.REFUND_CALL_BACK_URL);
        SortedMap<String, Object> amount = new TreeMap<>();
        amount.put("refund", price); //退款金额
        amount.put("total", total); //总金额
        amount.put("currency" ,"CNY");   //退款币种
        data.put("amount",amount);

        List<SortedMap<String, Object>> goodsDetails = new ArrayList<>();
        for (WxOrderGoodsEntity goods : goodsList) {
            SortedMap<String, Object> goodsDetail = new TreeMap<>();
            goodsDetails.add(goodsDetail);
            goodsDetail.put("merchant_goods_id", goods.getMerchantGoodsId()); //商品编号
            goodsDetail.put("goods_name", goods.getGoodsName());    //商品名称
            goodsDetail.put("unit_price", goods.getUnitPrice());    //商品单价
            goodsDetail.put("refund_amount", goods.getRefundAmount()); //商品退款金额
            goodsDetail.put("refund_quantity", goods.getRefundQuantity());   //退货数量
        }
        data.put("goods_detail", goodsDetails);


        //获取证书私钥
        PrivateKey key = certificateService.getPrivateKey();
        try {
            //计算鉴权信息
            String nonce = WxPayUtil.createNonce();
            long timestamp = System.currentTimeMillis() / 1000;
            String sign = WxPayUtil.getSign("POST", "/v3/refund/domestic/refunds",timestamp, nonce, data, key);
            String authInfo = "mchid=\"" + WxConfig.MCH_ID + "\","
                    + "nonce_str=\"" + nonce + "\","
                    + "timestamp=\"" + timestamp + "\","
                    + "serial_no=\"" + WxConfig.CERTIFICATE_SERIAL_NO + "\","
                    + "signature=\"" + sign + "\"";
            //设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "WECHATPAY2-SHA256-RSA2048 " + authInfo);
            HttpEntity<SortedMap<String, Object>> req = new HttpEntity<>(data, headers);
            String res = restTemplateForWxService.postForObject(WX_REFUND_API, req, String.class);
            return JSONObject.parseObject(res);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 解密资源数据
     * @param resource 通知资源数据
     * @return 通知参数
     */
    private JSONObject decryptResource(JSONObject resource){
        if(!resource.containsKey("associated_data")
                || !resource.containsKey("nonce")
                ||!resource.containsKey("ciphertext")
        ) return null;
        try {
            return JSONObject.parseObject(new AesUtil(WxConfig.API_V3_KEY.getBytes()).decryptToString(
                    resource.getString("associated_data").getBytes(),
                    resource.getString("nonce").getBytes(),
                    resource.getString("ciphertext")
            ));
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 退款失败
     */
    private JSONObject onRefundError(JSONObject resource, String message, LocalDateTime time){
        JSONObject notice = decryptResource(resource);
        if(notice == null || !notice.containsKey("out_refund_no")){
            JSONObject err = new JSONObject();
            err.put("code", "UNKNOWN_RESOURCE");
            return err;
        }
        String outRefundNo = notice.getString("out_refund_no");
        return orderService.onWxRefundFail(outRefundNo, message, time);
    }

    /**
     * 退款成功
     */
    private JSONObject onRefundSuccess(JSONObject resource, LocalDateTime time){
        JSONObject notice = decryptResource(resource);
        if(notice == null || !notice.containsKey("out_refund_no")){
            JSONObject err = new JSONObject();
            err.put("code", "UNKNOWN_RESOURCE");
            return err;
        }
        String outRefundNo = notice.getString("out_refund_no");
        String refundNo = notice.getString("refund_id");
        String refundStatus = notice.getString("refund_status");
        LocalDateTime refundTime = LocalDateTime.parse(notice.getString("success_time"),formatter);
        JSONObject amount = notice.getJSONObject("amount");
        BigDecimal total = BigDecimal.valueOf(amount.getInteger("payer_total")*100);
        BigDecimal refund = BigDecimal.valueOf(amount.getInteger("payer_refund")*100);

        if(refundStatus.equals("SUCCESS")){
            return orderService.onWxRefundSuccess(outRefundNo, refundNo, refundTime, total, refund);
        }
        return orderService.onWxRefundFail(outRefundNo,
                refundStatus.equals("CLOSED") ? "订单已关闭" :
                        refundStatus.equals("ABNORMAL") ? "账户异常" : "",
                time
        );
    }

    /**
     * 微信退款回调
     */
    public JSONObject notify(JSONObject data) {
        if(data.containsKey("event_type") && data.containsKey("resource")) {
            switch (data.getString("event_type")) {
                case "REFUND.SUCCESS" -> {
                    return onRefundSuccess(data.getJSONObject("resource"),
                            LocalDateTime.parse(data.getString("create_time"),formatter)
                    );
                }
                case "REFUND.ABNORMAL" -> {
                    return onRefundError(data.getJSONObject("resource"),
                            data.getString("summary"),
                            LocalDateTime.parse(data.getString("create_time"),formatter)
                    );
                }
            }
        }
        JSONObject err = new JSONObject();
        err.put("code", "UNKNOWN_DATA");
        return err;
    }
}
