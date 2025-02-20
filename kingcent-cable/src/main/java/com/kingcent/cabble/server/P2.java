package com.kingcent.cabble.server;

import com.kingcent.cabble.server.exception.CableMessageException;
import com.kingcent.cabble.server.messge.CableMessage;
import com.kingcent.cabble.server.messge.CableMessageHead;
import com.kingcent.cabble.server.messge.HelloInfo;
import com.kingcent.cabble.server.utils.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class P2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        HelloInfo helloInfo = new HelloInfo("shop", "11223344");
        CableMessage helloMsg = CableMessage.hello(helloInfo);
        System.out.println(new String(helloMsg.getBytes(), StandardCharsets.UTF_8));
//        Socket socket = new Socket("localhost",8889);
        Socket socket = new Socket("119.29.76.76",8889);
        socket.getOutputStream().write(helloMsg.getBytes());
        InputStream inputStream = socket.getInputStream();
        while (true){
            SocketUtil.read(inputStream, CableMessageHead.SIZE,(h) -> {
                System.out.println("-------------------------------------");
                System.out.println(new String(h,StandardCharsets.UTF_8));
                System.out.println("-------------------------------------");
                CableMessageHead cableMessageHead = null;
                try {
                    cableMessageHead = CableMessageHead.fromBytes(h);
                    CableMessageHead finalCableMessageHead = cableMessageHead;
                    SocketUtil.read(inputStream, cableMessageHead.getLength(),(data) -> {
                    System.out.println("======================================");
                    System.out.println("->->"+ finalCableMessageHead.getLength()+":"+data.length);
                    System.out.println(new String(data, StandardCharsets.UTF_8));
                    System.out.println("======================================");
                    });
//                    byte[] bytes = SocketUtil.readSync(inputStream, cableMessageHead.getLength());
//                    if(bytes == null){
//                        System.out.println("错误");
//                        return;
//                    }
                } catch (CableMessageException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
