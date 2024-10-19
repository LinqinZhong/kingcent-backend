package com.kingcent.common.wx.service;

import com.kingcent.common.wx.config.WxConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @author rainkyzhong
 * @date 2023/8/19 1:14
 */
@Slf4j
public class WxCertificateService {

    /**
     * 获取私钥
     */
    public PrivateKey getPrivateKey(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try(InputStream inputStream = classLoader.getResourceAsStream(WxConfig.CERTIFICATE_PATH)) {
            if (inputStream == null){
                throw new RuntimeException("证书文件不存在，请将证书放到"+WxConfig.CERTIFICATE_PATH+"资源路径下");
            }
            KeyStore keyStore;
            keyStore = KeyStore.getInstance("PKCS12");
            //装载证书资源文件
            keyStore.load(inputStream, WxConfig.MCH_ID.toCharArray());
            return (PrivateKey) keyStore.getKey(WxConfig.CERTIFICATE_KEY_ALIAS, WxConfig.MCH_ID.toCharArray());
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyStoreException |
                 UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
