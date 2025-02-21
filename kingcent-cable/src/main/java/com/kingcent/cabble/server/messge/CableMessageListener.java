package com.kingcent.cabble.server.messge;

public interface CableMessageListener {
    void onForward(CableMessageHead head, byte[] data);

    void onOuterClose(CableMessageHead head);

    void onForwardCompleted(CableMessageHead head);

    void onListenEnd();
}
