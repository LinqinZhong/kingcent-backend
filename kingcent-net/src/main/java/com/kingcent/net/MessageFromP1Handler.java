package com.kingcent.net;

import java.io.IOException;
import java.net.Socket;

public interface MessageFromP1Handler {
    void onHandMessage(HandMessageHead head, byte[] data, Response response) throws IOException;
    void onClientClose();
}
