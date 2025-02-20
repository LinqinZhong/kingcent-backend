package com.kingcent.cabble.server.utils;


import com.kingcent.cabble.server.messge.CableMessage;
import com.kingcent.cabble.server.messge.CableMessageHead;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketUtil {

    public interface ReadHandler{
        void onRead(byte[] data);
    }
    public static byte[] readSync(InputStream inputStream, int len) throws IOException {
        byte[] buffer = new byte[len];
        int read = inputStream.read(buffer);
        if(read == -1) return null;
        if(len <= read) return buffer;
        byte[] data = new byte[read];
        System.arraycopy(buffer,0,data,0,read);
        return data;
    }

    public static void read(InputStream inputStream, int len, ReadHandler handler) throws IOException {
        while (len > 0){
            byte[] buffer = new byte[Math.min(len, 1024)];
            int read = inputStream.read(buffer);
            len -= read;
            handler.onRead(buffer);
            if(read == -1) break;
        }
    }

    public static void read(InputStream inputStream, ReadHandler handler) throws IOException {
        int SIZE = 8 * CableMessageHead.SIZE;
        while (true){
            byte[] buffer = new byte[SIZE];
            int read = inputStream.read(buffer);
            if(read == -1) return;
            if(read < SIZE){
                byte[] data = new byte[read];
                System.arraycopy(buffer,0,data,0,read);
                handler.onRead(data);
                continue;
            }
            handler.onRead(buffer);
        }
    }
}
