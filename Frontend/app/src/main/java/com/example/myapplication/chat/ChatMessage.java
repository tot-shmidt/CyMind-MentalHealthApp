package com.example.myapplication.chat;

import java.time.LocalDateTime;

public class ChatMessage {
    private String messageId;
    private String groupId;
    private int senderId;
    private String senderName;
    private String content;
    private String timestamp;
    private String messageType;
    private boolean isSentByCurrentUser;

    // Constructor for local messages (backward compatible)
    public ChatMessage(int senderId, String content, String timestamp, boolean isSentByCurrentUser) {
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.groupId = null;
        this.messageId = null;
        this.messageType = "MESSAGE";
        this.senderName = null;
    }

    // Constructor for backend messages (includes groupId)
    public ChatMessage(String groupId, int senderId, String content, String timestamp, boolean isSentByCurrentUser) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.messageId = null;
        this.messageType = "MESSAGE";
        this.senderName = null;
    }

    // Full constructor with all fields
    public ChatMessage(String messageId, String groupId, int senderId, String content, String timestamp, String messageType, boolean isSentByCurrentUser) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.senderName = null;
    }

    // Full constructor with name field
    public ChatMessage(String messageId, String groupId, int senderId, String senderName, String content, String timestamp, String messageType, boolean isSentByCurrentUser) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.isSentByCurrentUser = isSentByCurrentUser;
    }

    public String getMessageId() { return messageId; }
    public String getGroupId() { return groupId; }
    public int getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return LocalDateTime.parse(timestamp); }
    public String getMessageType() { return messageType; }
    public boolean isSentByCurrentUser() { return isSentByCurrentUser; }

    public void setMessageId(String messageId) { this.messageId = messageId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
}