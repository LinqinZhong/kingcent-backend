package com.kingcent.cabble.server;

import java.net.Socket;

public interface P2Handler {
    void onServiceNotFound();

    void onServiceBusy();

    void onReply(byte[] data);

    void onServiceEnd();

    void onServiceProvide(Socket p2);
}
