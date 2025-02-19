package com.kingcent.net;

import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConnectionPool {
    private final Map<String, List<Socket>> usageP2Map = new HashMap<>();

    private final Map<Socket,String> p2NameMap = new HashMap<>();
    private final Map<Socket, List<Socket>> p2LocationMap = new HashMap<>();
    private final Map<String,Socket> p2OfClientMap = new HashMap<>();

    public void addP2(String name, Socket socket){
        List<Socket> sockets = this.usageP2Map.computeIfAbsent(name, (r) -> new LinkedList<>());
        p2LocationMap.put(socket, sockets);
        sockets.add(socket);
        p2NameMap.put(socket, name);
    }

    synchronized public Socket getP2(String clientName, String name) throws InterruptedException {
        List<Socket> sockets = this.usageP2Map.get(name);
        if(sockets == null) return null;
        if(sockets.isEmpty()) return null;
        Socket p2 = sockets.get(0);
        p2OfClientMap.put(clientName,p2);
        return p2;
    }

    public Socket getP2OfClient(String clientName){
        return p2OfClientMap.get(clientName);
    }

    public String remove(Socket p2) {
        List<Socket> sockets = p2LocationMap.remove(p2);
        if(sockets != null){
            sockets.remove(p2);
        }
        return p2NameMap.remove(p2);
    }
}
