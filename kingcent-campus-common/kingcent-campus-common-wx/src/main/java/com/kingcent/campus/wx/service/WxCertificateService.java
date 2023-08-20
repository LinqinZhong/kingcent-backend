package com.kingcent.campus.wx.service;

import com.kingcent.campus.wx.config.WxConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @author rainkyzhong
 * @date 2023/8/19 1:14
 */
@Slf4j
@Service
public class WxCertificateService {

    /**
     * 获取私钥
     */
    public PrivateKey getPrivateKey() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(WxConfig.CERTIFICATE_PATH);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            //装载证书资源文件
            keyStore.load(inputStream, WxConfig.MCH_ID.toCharArray());
        } finally {
            assert inputStream != null;
            inputStream.close();
        }
        return (PrivateKey) keyStore.getKey(WxConfig.CERTIFICATE_KEY_ALIAS, WxConfig.MCH_ID.toCharArray());
    }

}
