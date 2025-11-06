package com.example.myapplication.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.lifecycle.Observer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
                    Log.d(key, "Connected to WebSocket");
                }

                @Override
                public void onMessage(String message) {
                    Log.d(key, "Received message: " + message);

                    try {
                        // Check if it's an array (initial message history) or single object
                        if (message.trim().startsWith("[")) {
                            // Initial message history - array of messages
                            JSONArray messageArray = new JSONArray(message);
                            for (int i = 0; i < messageArray.length(); i++) {
                                JSONObject msgObj = messageArray.getJSONObject(i);
                                // Post each message to ChatManager
                                chatManager.postMessage(key, msgObj.toString());
                            }
                            Log.d(key, "Loaded " + messageArray.length() + " historical messages");
                        } else {
                            // Single incoming message - {groupId, senderId, content, timestamp}
                            JSONObject msgObj = new JSONObject(message);
                            chatManager.postMessage(key, message);
                            Log.d(key, "Received new message from sender: " + msgObj.optInt("senderId", -1));
                        }
                    } catch (JSONException e) {
                        Log.e(key, "Error parsing message JSON: " + e.getMessage());
                        // If JSON parsing fails, post raw message as fallback
                        chatManager.postMessage(key, message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(key, "WebSocket closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(key, "WebSocket error: " + ex.getMessage());
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