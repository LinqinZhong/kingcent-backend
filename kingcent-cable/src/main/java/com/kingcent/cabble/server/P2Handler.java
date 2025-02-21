package com.kingcent.cabble.server;

import java.net.Socket;

public interface P2Handler {
    void onServiceNotFound();

    void onServiceBusy();

    void onReply(byte[] data);

    void onServerClosed();

    void onServiceProvide(Socket p2);

    void onServiceReadyToEnd();
}
