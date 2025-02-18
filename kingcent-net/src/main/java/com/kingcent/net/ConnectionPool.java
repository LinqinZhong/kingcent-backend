package com.kingcent.net;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionPool {
    private Map<String, List<Socket>> map = new HashMap<>();
    public ConnectionPool(){
        // TODO 用多个P2连到P1，以应对并发问题
    }
}
