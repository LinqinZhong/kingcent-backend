package com.kingcent.cabble.server;

import cn.hutool.db.meta.Table;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {

    private static ConnectionPool instance;

    // 可用的P2列表
    private final Map<String, LinkedBlockingQueue<Socket>> p2OfServices = new ConcurrentHashMap<>();

    // 客户端绑定的P2Handler
    private final Map<String,P2Handler> handlerOfClient = new ConcurrentHashMap<>();

    public static ConnectionPool getInstance(){
        ConnectionPool connectionPool = instance == null ? instance = new ConnectionPool() : instance;
        Logger.info(connectionPool+"");
        return connectionPool;
    }

    public void addP2(String serviceName, Socket p2){
        Logger.info("add-p2: "+serviceName);
        LinkedBlockingQueue<Socket> p2s;
        if(p2OfServices.containsKey(serviceName)){
            p2s = p2OfServices.get(serviceName);
        }else{
            p2s = new LinkedBlockingQueue<>();
            p2OfServices.put(serviceName, p2s);
        }
        p2s.add(p2);
    }

    // 获取client绑定的的handler
    public P2Handler getHandlerOfClient(String clientHost, int clientPort){
        return handlerOfClient.get(clientHost+":"+clientPort);
    }

    public void useP2(String clientHost, int clientPort, String serviceName, P2Handler p2Handler){
        LinkedBlockingQueue<Socket> p2s = p2OfServices.get(serviceName);
        if(p2s == null) {
            // 服务未发现
            p2Handler.onServiceNotFound();
            return;
        }
//        try {
//
//            Logger.info("重新入队");
//            // p2重新入队
//            try {
//                p2s.put(p2);
//            } catch (InterruptedException e) {
//                // TODO 可能有bug
//                // 入队失败，关闭p2
//                Logger.info("关闭p2");
//                try {
//                    p2.close();
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        } catch (InterruptedException e) {
//            // 阻塞被打断（服务忙）
//            p2Handler.onServiceBusy();
//        }

        // 取出一个P2
        Socket p2 = p2s.peek();
        // 记录client绑定的p2
        String clientName = clientHost+":"+clientPort;
        handlerOfClient.put(clientName, p2Handler);
        new Thread(() -> {
            // 为调用者提供服务
            p2Handler.onServiceProvide(p2);
            // 准备结束的时候调用
            p2Handler.onServiceReadyToEnd();
            // 清除p2的服务
            handlerOfClient.remove(clientName);
        }).start();
    }
}
