package com.example.secondplatform;

public class FromUserMessage {

    private String fromUserId;
    private String username;
    private int unReadNum;

    public FromUserMessage(String fromUserId, String username, int unReadNum) {
        this.fromUserId = fromUserId;
        this.username = username;
        this.unReadNum = unReadNum;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getUsername() {
        return username;
    }

    public int getUnReadNum() {
        return unReadNum;
    }
}
