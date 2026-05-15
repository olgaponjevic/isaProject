package com.example.isaProtobufServer.dto;

public class MessageResponse {
    private int id;
    private String content;
    private long timestamp;
    private String serverInfo;

    public MessageResponse() {}

    public MessageResponse(int id, String content, long timestamp, String serverInfo) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.serverInfo = serverInfo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getServerInfo() { return serverInfo; }
    public void setServerInfo(String serverInfo) { this.serverInfo = serverInfo; }
}