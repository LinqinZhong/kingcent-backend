package com.kingcent.net;

import java.io.IOException;

public interface Response{
    void send(byte[] data) throws IOException;
}