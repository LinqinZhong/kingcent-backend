package com.kingcent.campus.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.RefundOrderMapService;
import com.kingcent.campus.service.RefundOrderService;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.OrderGoodsEntity;
import com.kingcent.campus.shop.entity.RefundOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import static com.kingcent.campus.shop.util.WxPayUtil.createNonce;
import static com.kingcent.campus.shop.util.WxPayUtil.getSign;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:22
 * @warning 支付敏感信息，注意保密，禁止外传，侵权必究
 */
@Service
@Slf4j
public class AppWxRefundService {

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private RefundOrderMapService refundOrderMapService;

    @Autowired
    private AppWxCertificateService certificateService;

    @Autowired
    private RestTemplate restTemplate;

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
     * 退款回调地址
     */
    @Value("https://a.intapter.cn/shop/order/wx_refund_notify_2023_8")
    private String REFUND_CALL_BACK_URL;

    /**
     * 退款接口
     */
    private final static String WX_REFUND_API = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";


    /**
     * 证书序列号
     */
    @Value("${third.wechat.certificate-serial-no}")
    private String CER_SERIAL_NO;


    /**
     * 发起退款请求
     */
    public JSONObject requestToRefund(
            String tradeNo,
            String outRefundNo,
            long orderPrice,
            long payPrice,
            List<OrderGoodsEntity> goodsList,
            String reason
    ) throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        SortedMap<String, Object> data = new TreeMap<>();
        data.put("transaction_id" ,tradeNo);
        data.put("out_refund_no", outRefundNo);
        data.put("reason",reason);
        data.put("notify_url", REFUND_CALL_BACK_URL);
        SortedMap<String, Object> amount = new TreeMap<>();
        amount.put("refund", orderPrice); //退款金额
        amount.put("total", orderPrice); //原订单金额
        amount.put("currency" ,"CNY");   //退款币种
        data.put("amount",amount);

        List<SortedMap<String, Object>> goodsDetails = new ArrayList<>();
        for (OrderGoodsEntity goods : goodsList) {
            SortedMap<String, Object> goodsDetail = new TreeMap<>();
            goodsDetails.add(goodsDetail);
            goodsDetail.put("merchant_goods_id", goods.getId()+""); //商品编号
            goodsDetail.put("goods_name", goods.getTitle());    //商品名称
            goodsDetail.put("unit_price", goods.getUnitPrice().multiply(new BigDecimal(100)).longValue());    //商品单价
            goodsDetail.put("refund_amount", goods.getPrice().multiply(new BigDecimal(100)).longValue()); //商品退款金额
            goodsDetail.put("refund_quantity", goods.getCount());   //退货数量
        }
        data.put("goods_detail", goodsDetails);


        //获取证书私钥
        PrivateKey key;
        try {
             key = certificateService.getPrivateKey();
        } catch (UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        }

        try {
            //计算鉴权信息
            String nonce = createNonce();
            long timestamp = System.currentTimeMillis() / 1000;
            String sign = getSign("POST", "/v3/refund/domestic/refunds",timestamp, nonce, data, key);
            String authInfo = "mchid=\"" + WX_MCH_ID + "\","
                    + "nonce_str=\"" + nonce + "\","
                    + "timestamp=\"" + timestamp + "\","
                    + "serial_no=\"" + CER_SERIAL_NO + "\","
                    + "signature=\"" + sign + "\"";
            //设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "WECHATPAY2-SHA256-RSA2048 " + authInfo);
            HttpEntity<SortedMap<String, Object>> req = new HttpEntity<>(data, headers);
            String res = restTemplate.postForObject(WX_REFUND_API, req, String.class);
            return JSONObject.parseObject(res);
        } catch (InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}
