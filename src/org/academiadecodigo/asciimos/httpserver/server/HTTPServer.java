package org.academiadecodigo.asciimos.httpserver.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {

    public void listen(int port) {
        try {
            Socket socket;
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                socket = serverSocket.accept();
                answerClient(socket);
            }

        } catch (Exception e) {
            System.out.println("An error has occurred.");
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            System.out.println("Error sending the packet");
            e.printStackTrace();
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
        if (fileName.contains(".html")) {
            return "text/html; charset=UTF-8";
        }

        if (fileName.contains(".mp4")) {
            return "video/mp4";
        }

        if (fileName.contains(".png") || fileName.contains(".jpg") || fileName.contains(".gif")) {
            String[] splited = fileName.split("\\.");
            return "image/" + splited[splited.length - 1];
        }

        return null;
    }
}