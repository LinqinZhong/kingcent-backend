package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.OrderEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public interface OrderService extends IService<OrderEntity> {
    Result<?> deleteOrder(Long id);
}
