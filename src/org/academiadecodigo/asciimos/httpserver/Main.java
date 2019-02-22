package org.academiadecodigo.asciimos.httpserver;

import org.academiadecodigo.asciimos.httpserver.server.HTTPServer;

public class Main {

    public static void main(String[] args) {
        HTTPServer server = new HTTPServer();
        server.listen(8080);
    }

}
