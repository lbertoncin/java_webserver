package org.academiadecodigo.asciimos.httpserver;

import org.academiadecodigo.asciimos.httpserver.server.ClientHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws IOException {
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true) {
            ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
            cachedPool.submit(clientHandler);
        }
    }
}
