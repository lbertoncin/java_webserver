package org.academiadecodigo.asciimos.httpserver.server;

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
        }
    }

    private void sendRequest(Socket socket, String path) {
        Response response = getFileByte(path);

        switch (response.getStatusCode()) {
            case 404:
                response = getFileByte("error/404.html");
                response.setStatusCode(CodeType.ERROR_404);
                break;
            case 500:
                response = getFileByte("error/500.html");
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

        System.out.println("Sending file: " + path);
        return path;
    }

    private String getContentType(String fileName) {
        String[] splited = fileName.split("\\.");
        String ext = splited[splited.length - 1];

        if (ext.equals("png") || ext.equals("jpg") || ext.equals("gif") || ext.equals("jpeg")) {
            return "image/" + ext;
        }

        if (ext.equals("html") || ext.equals("txt") || ext.equals("css")) {
            return "text/" + ext + "; charset=UTF-8";
        }

        if(ext.equals("mpeg") || ext.equals("ogg") || ext.equals("mp3")) {
            return "audio/" + ext;
        }

        if (ext.equals("mp4")) {
            return "video/" + ext;
        }

        // specific application that requires full name
        if(ext.equals("js")) {
            return "application/javascript";
        }

        return "*";
    }
}