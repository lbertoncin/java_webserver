package org.academiadecodigo.asciimos.httpserver.server;

import java.util.Arrays;

public class Response {
    private byte[] data;
    private String contentType;
    private CodeType statusCode;

    public Response(byte[] data, String contentType, CodeType statusCode) {
        this.data = data;
        this.contentType = contentType;
        this.statusCode = statusCode;
    }

    public byte[] getData() {
        return data;
    }

    public int getStatusCode() {
        return statusCode.getCode();
    }

    public String getContentType() {
        return contentType;
    }

    public int getLength() {
        return data.length;
    }

    public void setStatusCode(CodeType statusCode) {
        this.statusCode = statusCode;
    }
}
