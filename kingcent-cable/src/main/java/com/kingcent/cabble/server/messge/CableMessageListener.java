package com.kingcent.cabble.server.messge;

public interface CableMessageListener {
    void onForward(CableMessageHead head, byte[] data);

    void onOuterClose();

    void onForwardCompleted();

    void onListenEnd();
}
