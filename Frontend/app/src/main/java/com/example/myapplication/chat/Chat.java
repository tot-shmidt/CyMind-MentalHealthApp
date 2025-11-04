package com.example.myapplication.chat;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private String chatId;             // Unique identifier for this chat
    private String name;               // Chat group name
    private List<String> members;      // List of user IDs or emails
    private List<Message> messages;    // List of messages

    public Chat(String chatId, String name) {
        this.chatId = chatId;
        this.name = name;
        this.members = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    // --- Getters ---
    public String getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    // --- Setters / Modifiers ---
    public void setName(String name) {
        this.name = name;
    }

    public void addMember(String member) {
        if (!members.contains(member)) {
            members.add(member);
        }
    }

    public void removeMember(String member) {
        members.remove(member);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
