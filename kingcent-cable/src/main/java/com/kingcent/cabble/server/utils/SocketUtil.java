package com.kingcent.cabble.server.utils;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketUtil {

    public interface ReadHandler{
        void onRead(byte[] data);
    }
    public static byte[] readSync(Socket socket, int len) throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[len];
        int read = inputStream.read(buffer);
        if(read == -1) return null;
        if(len == read) return buffer;
        byte[] data = new byte[read];
        System.arraycopy(buffer,0,data,0,len);
        return data;
    }

    public static void read(Socket socket, int len, ReadHandler handler) throws IOException {
        InputStream inputStream = socket.getInputStream();
        while (len > 0){
            byte[] buffer = new byte[Math.min(len, 1024)];
            int read = inputStream.read(buffer);
            len -= read;
            handler.onRead(buffer);
            if(read == -1) break;
        }
    }

    public static void read(Socket socket, ReadHandler handler) throws IOException {
        InputStream inputStream = socket.getInputStream();

        while (true){
            byte[] buffer = new byte[1024];
            int read = inputStream.read(buffer);
            handler.onRead(buffer);
            if(read == -1) return;
        }
    }
}
