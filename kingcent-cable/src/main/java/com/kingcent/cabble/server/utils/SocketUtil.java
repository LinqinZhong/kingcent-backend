package com.kingcent.cabble.server.utils;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketUtil {

    public interface ReadHandler{
        void onRead(byte[] data) throws IOException;
    }
    public static byte[] readSync(Socket socket, int len) throws IOException {
        int off = 0;
        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[len];
        while(len > 0){
            int read = inputStream.read(buffer, off, len);
            if(read == -1) return null;
            off += read;
            len -= read;
        }
        return buffer;
    }

    public static void read(Socket socket, int len, ReadHandler handler) throws IOException {
        InputStream inputStream = socket.getInputStream();
        while (len > 0){
            int bufferLen = Math.min(len, 1024);
            byte[] buffer = new byte[bufferLen];
            int read = inputStream.read(buffer);
            if(read == -1) break;
            if(read == bufferLen) {
                handler.onRead(buffer);
            } else {
                byte[] data = new byte[read];
                System.arraycopy(buffer, 0, data, 0, read);
                handler.onRead(data);
            }
            len -= read;
        }
    }

    public static void read(Socket socket, ReadHandler handler) throws IOException {
        InputStream inputStream = socket.getInputStream();
        int bufferLen = 1024;
        while (true){
            byte[] buffer = new byte[bufferLen];
            int read = inputStream.read(buffer);
            if(read == -1) return;
            if(read == bufferLen){
                handler.onRead(buffer);
            } else {
                byte[] data = new byte[read];
                System.arraycopy(buffer, 0, data, 0, read);
                handler.onRead(data);
            }
        }
    }
}
