package com.example.isaProtobufServer.dto;

public class MessageRequest {
    private int id;
    private String content;
    private long timestamp;

    public MessageRequest() {}

    public MessageRequest(int id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}