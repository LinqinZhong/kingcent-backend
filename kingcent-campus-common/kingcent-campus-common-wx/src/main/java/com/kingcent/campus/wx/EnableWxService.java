package com.kingcent.campus.wx;

import com.kingcent.campus.wx.service.*;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 微信服务模块，用于支持微信接口的对接
 * 目前包括：
 * 1.微信小程序用户服务
 * 2.微信支付
 * 使用此模块时，需加上此注解
 * @author rainkyzhong
 * @date 2023/8/20 17:57
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({WxCertificateService.class,
        WxPayService.class,
        WxRefundService.class,
        WxShippingService.class,
        WxTokenService.class
})
@Documented
public @interface EnableWxService {
}
