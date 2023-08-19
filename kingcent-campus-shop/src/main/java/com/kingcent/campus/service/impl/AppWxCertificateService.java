package com.kingcent.campus.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @author rainkyzhong
 * @date 2023/8/19 1:14
 * @warning 支付敏感信息，注意保密，禁止外传，侵权必究
 */
@Slf4j
@Service
public class AppWxCertificateService {

    /**
     * 证书存放路径
     */
    private final static String CER_PATH = "wxpay/apiclient_cert.p12";

    /**
     * 商户号
     */
    @Value("${third.wechat.mchid}")
    private String WX_MCH_ID;


    /**
     * 获取私钥
     */
    public PrivateKey getPrivateKey() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(CER_PATH);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            //装载证书资源文件
            keyStore.load(inputStream, WX_MCH_ID.toCharArray());
        } finally {
            assert inputStream != null;
            inputStream.close();
        }
        return (PrivateKey) keyStore.getKey("tenpay certificate", WX_MCH_ID.toCharArray());
    }

}
