package com.kingcent.cabble.server.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rainkyzhong
 * @date 2025/2/23 3:51
 */
public class HttpHeader {
    private byte[] data;

    public HttpHeader init(byte[] data){
        this.data = data;
        return this;
    }

    private static final byte[] GET = "GET".getBytes();
    private static final byte[] POST = "POST".getBytes();
    private static final byte[] PUT = "PUT".getBytes();
    private static final byte[] DELETE = "DELETE".getBytes();
    private static final byte[] HEAD = "HEAD".getBytes();
    private static final byte[] OPTIONS = "OPTIONS".getBytes();
    private static final byte[] TRACE = "TRACE".getBytes();

    private static final byte SPACE = ' ';
    private static final byte COLON = ':';
    private static final byte CR = '\r';
    private static final byte LF = '\n';

    public boolean isHttpRequestHeader() {
        if (data == null || data.length < 4) {
            return false;
        }

        // 检查请求方法
        if (!startsWithMethod(data)) {
            return false;
        }

        // 查找请求行结束
        int lineEndIndex = findLineEndIndex(data, 0);
        if (lineEndIndex == -1) {
            return false;
        }

        System.out.println("yes!!");

        // 检查是否有后续的头部字段和空行
        return true;
    }

    private boolean startsWithMethod(byte[] bytes) {
        return startsWith(bytes, GET) || startsWith(bytes, POST) || startsWith(bytes, PUT)
                || startsWith(bytes, DELETE) || startsWith(bytes, HEAD)
                || startsWith(bytes, OPTIONS) || startsWith(bytes, TRACE);
    }

    private boolean startsWith(byte[] bytes, byte[] prefix) {
        if (bytes.length < prefix.length || bytes[prefix.length] != SPACE) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (bytes[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private int findLineEndIndex(byte[] bytes, int startIndex) {
        for (int i = startIndex; i < bytes.length - 1; i++) {
            if (bytes[i] == CR && bytes[i + 1] == LF) {
                return i;
            }
        }
        return -1;
    }

    private boolean hasValidHeadersAndEmptyLine(byte[] bytes, int startIndex) {
        int currentIndex = startIndex;
        while (currentIndex < bytes.length - 1) {
            // 查找行结束
            int lineEndIndex = findLineEndIndex(bytes, currentIndex);
            if (lineEndIndex == -1) {
                return false;
            }

            // 检查是否为空行
            if (lineEndIndex == currentIndex) {
                return true;
            }

            // 检查头部字段是否包含冒号
            boolean hasColon = false;
            for (int i = currentIndex; i < lineEndIndex; i++) {
                if (bytes[i] == COLON) {
                    hasColon = true;
                    break;
                }
            }
            if (!hasColon) {
                return false;
            }

            currentIndex = lineEndIndex + 2;
        }
        return false;
    }

    private String text;

    private String text(){
        if(text == null){
            text = new String(data);
        }
        return text;
    }

    public HttpHeader setHost(String host){
        Pattern pattern = Pattern.compile("(?i)Host: [^\r\n]+");
        Matcher matcher = pattern.matcher(text());
        text = matcher.replaceAll("Host: "+host);
        data = text.getBytes();
        return this;
    }

    public byte[] getBytes(){
        return data;
    }
}
