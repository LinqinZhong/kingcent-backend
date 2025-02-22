package com.kingcent.cabble.server.messge;

public enum CableMessageType {

    // 招呼消息
    HELLO(0),
    // 转发消息
    FORWARD(1),
    // 外部关闭
    OUTER_CLOSE(2),
    // 心跳
    PING_PONG(3);

    public final int value;
    CableMessageType(int v){
        this.value = v;
    }
}
