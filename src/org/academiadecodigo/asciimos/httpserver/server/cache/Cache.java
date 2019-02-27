package org.academiadecodigo.asciimos.httpserver.server.cache;

import org.academiadecodigo.asciimos.httpserver.server.Response;

public class Cache {
    private String path;
    private Response response;

    public Cache(String path, Response response) {
        this.path = path;
        this.response = response;
    }

    public String getPath() {
        return path;
    }

    public Response getResponse() {
        return response;
    }
}
