package com.kingcent.cabble.server;

import com.kingcent.cabble.server.messge.CableMessage;
import com.kingcent.cabble.server.messge.HelloInfo;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class P2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        HelloInfo helloInfo = new HelloInfo("shop", "11223344");
        CableMessage helloMsg = CableMessage.hello(helloInfo);
        System.out.println(new String(helloMsg.getBytes(), StandardCharsets.UTF_8));
        Socket socket = new Socket("localhost",8889);
//        Socket socket = new Socket("119.29.76.76",8889);
        socket.getOutputStream().write(helloMsg.getBytes());

        int r;
        while ((r = socket.getInputStream().read()) != -1){
            System.out.println(r);
        }
    }
}
