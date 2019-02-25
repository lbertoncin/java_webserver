package org.academiadecodigo.asciimos.httpserver;

import org.academiadecodigo.asciimos.httpserver.server.ClientHandler;
import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true) {
            ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }
}
