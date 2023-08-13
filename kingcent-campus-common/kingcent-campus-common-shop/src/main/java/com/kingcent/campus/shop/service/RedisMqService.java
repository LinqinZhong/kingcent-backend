package com.kingcent.campus.shop.service;
 
public interface RedisMqService {
 
    void produce(String string);
 
    void consume();
}
 