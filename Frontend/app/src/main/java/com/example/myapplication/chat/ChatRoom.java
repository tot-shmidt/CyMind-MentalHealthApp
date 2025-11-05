package com.example.myapplication.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private String chatId;
    private String chatName;
    private int creatorId; // Student who created the chat
    private List<Integer> professionalIds;
    private String webSocketUrl;
    private long createdTimestamp;
    private String lastMessage;
    private long lastMessageTimestamp;

    public ChatRoom(String chatId, String chatName, List<Integer> professionalIds, String webSocketUrl) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.creatorId = -1; // Will be set separately
        this.professionalIds = professionalIds != null ? professionalIds : new ArrayList<>();
        this.webSocketUrl = webSocketUrl;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastMessage = "";
        this.lastMessageTimestamp = 0;
    }

    // Getters
    public String getChatId() { return chatId; }
    public String getChatName() { return chatName; }
    public int getCreatorId() { return creatorId; }
    public List<Integer> getProfessionalIds() { return professionalIds; }
    public String getWebSocketUrl() { return webSocketUrl; }
    public long getCreatedTimestamp() { return createdTimestamp; }
    public String getLastMessage() { return lastMessage; }
    public long getLastMessageTimestamp() { return lastMessageTimestamp; }

    // Setters
    public void setChatName(String chatName) { this.chatName = chatName; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = System.currentTimeMillis();
    }
    public void addProfessional(int professionalId) {
        if (!professionalIds.contains(professionalId)) {
            professionalIds.add(professionalId);
        }
    }
}