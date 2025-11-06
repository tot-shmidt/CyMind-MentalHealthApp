package com.example.myapplication.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
    private Handler mainHandler;

    public WebSocketService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        chatManager = ChatManager.getInstance();
        mainHandler = new Handler(Looper.getMainLooper());

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
                            // Format: [{messageId, senderId, content, timestamp, messageType}, ...]
                            JSONArray messageArray = new JSONArray(message);
                            Log.d(key, "Received array of " + messageArray.length() + " messages");

                            // Post each message to main thread with slight delay to prevent LiveData dropping
                            for (int i = 0; i < messageArray.length(); i++) {
                                final int index = i;
                                final String msgStr;
                                try {
                                    msgStr = messageArray.getJSONObject(i).toString();
                                } catch (JSONException e) {
                                    Log.e(key, "Error getting message at index " + i, e);
                                    continue;
                                }

                                // Post to main thread with incremental delay (50ms per message)
                                mainHandler.postDelayed(() -> {
                                    Log.d(key, "Posting message " + (index + 1) + "/" + messageArray.length() + " to ChatManager: " + msgStr);
                                    chatManager.postMessage(key, msgStr);
                                }, i * 50L);
                            }
                            Log.d(key, "Scheduled " + messageArray.length() + " historical messages for delivery");
                        } else {
                            // Single incoming message
                            // Format: {messageId, senderId, content, timestamp, messageType}
                            JSONObject msgObj = new JSONObject(message);
                            String messageType = msgObj.optString("messageType", "MESSAGE");

                            Log.d(key, "Received message type: " + messageType + " from sender: " + msgObj.optInt("senderId", -1));

                            // Handle different message types
                            switch (messageType) {
                                case "MESSAGE":
                                case "EDIT":
                                    // Regular message or edited message - post to ChatManager
                                    chatManager.postMessage(key, message);
                                    break;
                                case "DELETE":
                                    // Delete message - post to ChatManager for handling
                                    chatManager.postMessage(key, message);
                                    break;
                                default:
                                    Log.w(key, "Unknown message type: " + messageType);
                                    chatManager.postMessage(key, message);
                                    break;
                            }
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