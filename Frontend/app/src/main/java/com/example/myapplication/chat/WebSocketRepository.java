package com.example.myapplication.chat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketRepository {

    private static WebSocketRepository instance;
    private final MutableLiveData<Map<String, List<String>>> messages = new MutableLiveData<>(new HashMap<>());
    private Context appContext;

    private WebSocketService service;  // bound service reference

    private WebSocketRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static synchronized WebSocketRepository getInstance(Context context) {
        if (instance == null) {
            instance = new WebSocketRepository(context);
        }
        return instance;
    }

    public LiveData<Map<String, List<String>>> getMessages() {
        return messages;
    }

    public void addMessage(String key, String msg) {
        Map<String, List<String>> map = messages.getValue();
        if (map == null) map = new HashMap<>();
        List<String> chatMessages = map.getOrDefault(key, new ArrayList<>());
        chatMessages.add(msg);
        map.put(key, chatMessages);
        messages.postValue(map);
    }

    public void connect(String key, String url) {
        Intent intent = new Intent(appContext, WebSocketService.class);
        intent.setAction("CONNECT");
        intent.putExtra("key", key);
        intent.putExtra("url", url);
        appContext.startService(intent);
    }

    public void disconnect(String key) {
        Intent intent = new Intent(appContext, WebSocketService.class);
        intent.setAction("DISCONNECT");
        intent.putExtra("key", key);
        appContext.startService(intent);
    }

    public void sendMessage(String key, String message) {
        if (service != null) {
            service.sendToWebSocket(key, message);
        } else {
            Log.w("WebSocketRepository", "Service not yet bound, cannot send message.");
        }
    }

    // You’ll bind the service when the app starts (next step)
    public void setService(WebSocketService service) {
        this.service = service;
    }
}
