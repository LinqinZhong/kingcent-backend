package com.kingcent.cabble.server.utils;


import com.kingcent.cabble.server.messge.CableMessageHead;

import java.io.IOException;
import java.io.InputStream;

public class SocketUtil {

    public interface ReadHandler{
        void onRead(byte[] data);
    }

    // 得到数据长度一定为len，需要等待，读满len停止
    public static byte[] readSync(InputStream inputStream, int len) throws IOException {
        int off = 0;
        byte[] buffer = new byte[len];
        while (len > 0){
            int read = inputStream.read(buffer,off, len);
            if(read == -1) break;
            len -= read;
            off += read;
        }
        return buffer;
    }

    // 得到的数据长度不一定为len，有多个数据包，读满len为止
    public static void read(InputStream inputStream, int len, ReadHandler handler) throws IOException {
        while (len > 0){
            int l = Math.min(len, 1024);
            byte[] buffer = new byte[l];
            int read = inputStream.read(buffer);
            if(read == -1) break;
            len -= read;
            if(read < l){
                byte[] data = new byte[read];
                System.arraycopy(buffer,0,data,0,read);
                handler.onRead(data);
            }else{
                handler.onRead(buffer);
            }
        }
    }

    // 永无止境地读，直到流关闭
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
