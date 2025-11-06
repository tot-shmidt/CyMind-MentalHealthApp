package com.example.myapplication.chat;

public class ChatMember {
    private int userId;
    private String name;
    private String type; // "Student" or "Professional"
    private boolean isRemovable;

    public ChatMember(int userId, String name, String type, boolean isRemovable) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.isRemovable = isRemovable;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isRemovable() { return isRemovable; }
}
