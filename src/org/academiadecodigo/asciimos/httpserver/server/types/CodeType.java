package org.academiadecodigo.asciimos.httpserver.server.types;

public enum CodeType {
    ERROR_404(404),
    ERROR_500(500),
    OK_200(200);

    private int code;

    CodeType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
