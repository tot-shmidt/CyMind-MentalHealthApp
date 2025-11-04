package com.example.myapplication.chat;

public class Message {

    private String senderId;    // user ID or email
    private String senderName;  // <-- add senderName
    private String content;
    private long timestamp;
    private String chatKey;

    public Message(String senderId, String senderName, long timestamp, String chatKey) {
        this.senderId = senderId;
        this.senderName = senderName; // <-- initialize
        this.content = content;
        this.timestamp = timestamp;
        this.chatKey = chatKey;
    }

    // --- Getters ---
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }  // <-- add getter
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public String getChatKey() { return chatKey; }
}
