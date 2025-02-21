package com.kingcent.cabble.server;

public interface ServerRequestHandler {
    void onReply(byte[] data);

    void onClose();
}
