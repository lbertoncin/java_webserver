package org.academiadecodigo.asciimos.httpserver.server.types;

public enum ContentType {
    JS("application/javascript"),
    HTML("text/html"),
    TXT("text/html"),
    CSS("text/css"),
    PNG("image/png"),
    JPG("image/jpg"),
    GIF("image/gif"),
    JPEG("image/jpeg"),
    MPEG("audio/mpeg"),
    OGG("audio/ogg"),
    MP3("audio/mp3"),
    MP4("video/mp4");

    private String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getType() {
        return contentType;
    }
}
