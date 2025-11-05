package com.example.myapplication.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.lifecycle.Observer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebSocketService extends Service {

    private final Map<String, WebSocketClient> webSockets = new HashMap<>();
    private ChatManager chatManager;
    private Observer<ChatManager.MessageEvent> outgoingMessageObserver;

    public WebSocketService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        chatManager = ChatManager.getInstance();

        // Observe outgoing messages from activities
        outgoingMessageObserver = messageEvent -> {
            if (messageEvent != null) {
                WebSocketClient client = webSockets.get(messageEvent.chatId);
                if (client != null && client.isOpen()) {
                    client.send(messageEvent.message);
                    Log.d("WebSocketService", "Sent message for " + messageEvent.chatId);
                }
            }
        };

        chatManager.getOutgoingMessageEvent().observeForever(outgoingMessageObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("CONNECT".equals(action)) {
                String url = intent.getStringExtra("url");
                String key = intent.getStringExtra("key");
                connectWebSocket(key, url);
            } else if ("DISCONNECT".equals(action)) {
                String key = intent.getStringExtra("key");
                disconnectWebSocket(key);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (WebSocketClient client : webSockets.values()) {
            client.close();
        }
        if (outgoingMessageObserver != null) {
            chatManager.getOutgoingMessageEvent().removeObserver(outgoingMessageObserver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connectWebSocket(String key, String url) {
        try {
            URI serverUri = URI.create(url);
            WebSocketClient webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(key, "Connected");
                }

                @Override
                public void onMessage(String message) {
                    // Post message to ChatManager LiveData
                    chatManager.postMessage(key, message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(key, "Closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(key, "Error: " + ex.getMessage());
                }
            };

            webSocketClient.connect();
            webSockets.put(key, webSocketClient);
            Log.d("WebSocketService", "WebSocket connected for: " + key);

        } catch (Exception e) {
            Log.e("WebSocketService", "Connection error", e);
        }
    }

    private void disconnectWebSocket(String key) {
        WebSocketClient client = webSockets.get(key);
        if (client != null) {
            client.close();
            webSockets.remove(key);
        }
    }
}