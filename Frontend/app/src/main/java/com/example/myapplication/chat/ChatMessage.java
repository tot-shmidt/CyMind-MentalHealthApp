package com.example.myapplication.chat;

public class ChatMessage {
    private String groupId;
    private int senderId;
    private String content;
    private long timestamp;
    private boolean isSentByCurrentUser;

    // Constructor for local messages (backward compatible)
    public ChatMessage(int senderId, String content, long timestamp, boolean isSentByCurrentUser) {
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.groupId = null;
    }

    // Constructor for backend messages (includes groupId)
    public ChatMessage(String groupId, int senderId, String content, long timestamp, boolean isSentByCurrentUser) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
        this.isSentByCurrentUser = isSentByCurrentUser;
    }

    public String getGroupId() { return groupId; }
    public int getSenderId() { return senderId; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public boolean isSentByCurrentUser() { return isSentByCurrentUser; }

    public void setGroupId(String groupId) { this.groupId = groupId; }
}