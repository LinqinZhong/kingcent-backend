package com.kingcent.campus.wx.service;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.wx.config.WxConfig;
import com.kingcent.campus.wx.entity.WxOrderGoodsEntity;
import com.kingcent.campus.wx.util.WxPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:22
 */
@Service
@Slf4j
public class WxRefundService {

    @Autowired
    private WxCertificateService certificateService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 退款接口
     */
    private final static String WX_REFUND_API = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";



    /**
     * 发起退款请求
     */
    public JSONObject requestToRefund(
            String tradeNo,
            String outRefundNo,
            long price,
            List<WxOrderGoodsEntity> goodsList,
            String reason
    ) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        SortedMap<String, Object> data = new TreeMap<>();
        data.put("transaction_id" ,tradeNo);
        data.put("out_refund_no", outRefundNo);
        data.put("reason",reason);
        data.put("notify_url", WxConfig.REFUND_CALL_BACK_URL);
        SortedMap<String, Object> amount = new TreeMap<>();
        amount.put("refund", price); //退款金额
        amount.put("total", price); //原订单金额
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
        PrivateKey key;
        try {
             key = certificateService.getPrivateKey();
        } catch (UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        }

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
            String res = restTemplate.postForObject(WX_REFUND_API, req, String.class);
            return JSONObject.parseObject(res);
        } catch (InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}
