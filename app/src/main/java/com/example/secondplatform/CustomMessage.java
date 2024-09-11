package com.example.secondplatform;

public class CustomMessage {
    private String fromUsername;
    private String content;

    public CustomMessage(String fromUsername, String content) {
        this.fromUsername = fromUsername;
        this.content = content;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public String getContent() {
        return content;
    }
}
