package com.kingcent.net;

import java.io.IOException;
import java.net.Socket;

public interface MessageHandler{
    void onHandMessage(HandMessageHead head, byte[] data, Response response) throws IOException;

    void onMessage(byte[] data);

    void onP2SayHello(String ip, Socket socket);

    void onServerClose(HandMessageHead head);
}
