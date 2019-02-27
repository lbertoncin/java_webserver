package org.academiadecodigo.asciimos.httpserver.server;

import org.academiadecodigo.asciimos.httpserver.server.cache.CacheList;
import org.academiadecodigo.asciimos.httpserver.server.types.CodeType;
import org.academiadecodigo.asciimos.httpserver.server.types.ContentType;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        answerClient(clientSocket);
    }

    private void answerClient(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String header = reader.readLine();

            if (header == null) {
                System.out.println("Rejected connection, not a HTTP request.");
                return;
            }

            sendRequest(socket, getFilePath(header));
        } catch (Exception e) {
            System.out.println("Error occurred while sending the packet");
            e.printStackTrace();
        }
    }

    private void sendRequest(Socket socket, String path) {
        Response response = getFileByte(path);

        switch (response.getStatusCode()) {
            case 404:
                response = getCache("error/404.html");
                response.setStatusCode(CodeType.ERROR_404);
                break;
            case 500:
                response = getCache("error/500.html");
                response.setStatusCode(CodeType.ERROR_500);
                break;
        }

        String header = "HTTP/1.0 " + response.getStatusCode() + " Document Follows\r\n" +
                "Content-Type: " + response.getContentType() + "\r\n" +
                "Content-Length: " + response.getLength() + "\r\n" +
                "\r\n";

        try {
            OutputStream sender = socket.getOutputStream();
            sender.write(header.getBytes());
            sender.write(response.getData());
            sender.flush();
        } catch (SocketException e) {
            System.out.println("Connection has closed: " + socket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.out.println("An error has occured.");
        }

        System.out.println("Accepted request from: " + socket.getRemoteSocketAddress());
    }

    private Response getFileByte(String path) {

        try {
            BufferedInputStream bdata = new BufferedInputStream(new DataInputStream(new FileInputStream("files/" + path)));
            return new Response(bdata.readAllBytes(), getContentType(path), CodeType.OK_200);

        } catch (FileNotFoundException e) {
            System.out.println("File not found, sending 404!");
            return new Response(null, getContentType(path), CodeType.ERROR_404);

        } catch (IOException e) {
            System.out.println("Couldn't open the file for some unknown reason!");
            return new Response(null, getContentType(path), CodeType.ERROR_500);
        }
    }

    private String getFilePath(String header) {
        String[] requestSplited = header.split(" ");
        StringBuilder stringBuilder = new StringBuilder(requestSplited[1]);

        // delete char /
        String path = stringBuilder.deleteCharAt(0).toString();

        if (path.length() == 0) {
            path = "index.html";
        }
        return path;
    }

    private String getContentType(String fileName) {
        String[] splited = fileName.split("\\.");
        String ext = splited[splited.length - 1];

        ContentType[] types = ContentType.values();
        for (ContentType contentType : types) {
            if (contentType.toString().equals(ext.toUpperCase())) {
                return ContentType.valueOf(ext.toUpperCase()).getType();
            }
        }

        return "*/*";
    }

    private Response getCache(String path) {
        Response response = CacheList.find(path);
        if (response == null) {
            System.out.println("Serving file from disk, because it's not in cache: " + path);
            response = getFileByte(path);
            CacheList.add(path, response);
            return response;
        }
        System.out.println("Serving file from cache, IO has been saved: " + path);
        return response;
    }
}