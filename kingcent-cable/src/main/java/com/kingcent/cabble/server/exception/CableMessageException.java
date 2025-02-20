package com.kingcent.cabble.server.exception;

public class CableMessageException extends Exception {
    public static final CableMessageException ERROR_CABLE_MESSAGE_HEAD = new CableMessageException("Error cable message head.");
    public static CableMessageException NotAHelloMessageException = new CableMessageException("Cannot reply hello to an message which is not say hello.");
    public CableMessageException(String message){
        super(message);
    }
}
