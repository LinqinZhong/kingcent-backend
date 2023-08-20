package com.kingcent.campus.wx.config;


/**
 * 微信服务参数配置
 * @warning 本模块版权归武汉市江夏区青橙电商店所有，未经书面允许，禁止用于其它业务，违者追究法律责任
 * @warning 本文件涉及交易敏感信息，注意保密，禁止转发，侵权必究
 * @author rainkyzhong
 * @date 2023/8/20 3:33
 */
public class WxConfig {

    //小程序appid
    public final static String MINI_APP_ID =  "wx78ac22e61f72cba5";
    //微信商户号
    public final static String MCH_ID = "1650981448";
    //微信商户号证书存放路径
    public final static String CERTIFICATE_PATH = "wxpay/apiclient_cert.p12";
    //微信商户号证书密钥别名
    public final static String CERTIFICATE_KEY_ALIAS = "tenpay certificate";
    //微信商户号证书序列号
    public final static String CERTIFICATE_SERIAL_NO = "127E0622D57770C440C9F4D7ADE7F6E3E2F9DDE6";
    //微信支付回调地址
    public final static String PAYMENT_CALL_BACK_URL = "https://a.intapter.cn/shop/wx_notify/payment";
    //微信退款回调地址
    public final static String REFUND_CALL_BACK_URL = "https://a.intapter.cn/shop/wx_notify/refund";
    //微信支付APiv2密钥
    public final static String API_V2_KEY = "9620129cf275252ec9b3e9cd42e5bf56";
    //微信支付ApiV3密钥
    public final static String API_V3_KEY = "a36ecf4169cec2e43bd398e52b53a76b";
}
