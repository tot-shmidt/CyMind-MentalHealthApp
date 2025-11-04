package com.example.myapplication.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebSocketService extends Service {

    private final Map<String, WebSocketClient> webSockets = new HashMap<>();

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
                    Log.d("WebSocket", key + " connected");
                }

                @Override
                public void onMessage(String message) {
                    WebSocketRepository.getInstance(getApplicationContext()).addMessage(key, message);
                }


                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WebSocket", key + " closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    Log.e("WebSocket", key + " error: " + ex.getMessage());
                }
            };

            webSocketClient.connect();
            webSockets.put(key, webSocketClient);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnectWebSocket(String key) {
        WebSocketClient client = webSockets.get(key);
        if (client != null) {
            client.close();
            webSockets.remove(key);
        }
    }

    public void sendToWebSocket(String key, String message) {
        WebSocketClient webSocket = webSockets.get(key);
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.send(message);
        } else {
            Log.w("WebSocketService", "Tried to send message but socket is closed or null: " + key);
        }
    }

}
