package com.example.myapplication.chat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Chat>> chats = new MutableLiveData<>();
    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private final WebSocketRepository repo;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repo = WebSocketRepository.getInstance(application);

        // Placeholder: add mock chats
        List<Chat> mockChats = new ArrayList<>();
        mockChats.add(new Chat("chat1", "Math Group"));
        mockChats.add(new Chat("chat2", "Science Project"));
        chats.setValue(mockChats);

        messages.setValue(new ArrayList<>()); // initialize empty list
    }

    // --- Chats ---
    public LiveData<List<Chat>> getChats() {
        return chats;
    }

    public void addChat(Chat chat) {
        List<Chat> current = chats.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(chat);
        chats.setValue(current);
    }

    // --- Messages ---
    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public void addMessage(Message msg) {
        List<Message> current = messages.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(msg);
        messages.setValue(current);
    }

    // --- WebSocket ---
    public void connect(String key, String url) {
        repo.connect(key, url);
    }

    public void send(String key, String content) {
        // Add locally
        Message msg = new Message("currentUserId", content, System.currentTimeMillis(), key);
        addMessage(msg);

        // Send via WebSocket
        repo.sendMessage(key, content);
    }

    public void disconnect(String key) {
        repo.disconnect(key);
    }
}
