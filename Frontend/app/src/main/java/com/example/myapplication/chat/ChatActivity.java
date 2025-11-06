package com.example.myapplication.chat;

import static com.example.myapplication.Authorization.generateAuthToken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.myapplication.R;
import com.example.myapplication.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final String APP_API_URL = "http://coms-3090-066.class.las.iastate.edu:8080/";

    private RecyclerView messagesRv;
    private EditText messageEt, searchEt;
    private Button sendBtn, backBtn, infoBtn, searchToggleBtn, searchBtn, clearSearchBtn;
    private LinearLayout searchBarLayout;
    private TextView chatTitleTv;

    private String chatId;
    private String chatName;
    private ChatMessageAdapter messageAdapter;
    private List<ChatMessage> messages;
    private List<ChatMessage> allMessages; // Keep all messages for restore after search
    private ChatManager chatManager;
    private Observer<ChatManager.MessageEvent> messageObserver;
    private boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatManager = ChatManager.getInstance();

        // Get chat info from intent
        chatId = getIntent().getStringExtra("chatId");
        chatName = getIntent().getStringExtra("chatName");

        if (chatId == null) {
            Log.e(TAG, "No chatId provided");
            finish();
            return;
        }

        // Initialize UI elements
        messagesRv = findViewById(R.id.messagesRv);
        messageEt = findViewById(R.id.messageEt);
        searchEt = findViewById(R.id.searchEt);
        sendBtn = findViewById(R.id.sendBtn);
        backBtn = findViewById(R.id.backBtn);
        infoBtn = findViewById(R.id.infoBtn);
        searchToggleBtn = findViewById(R.id.searchToggleBtn);
        searchBtn = findViewById(R.id.searchBtn);
        clearSearchBtn = findViewById(R.id.clearSearchBtn);
        searchBarLayout = findViewById(R.id.searchBarLayout);
        chatTitleTv = findViewById(R.id.chatTitleTv);

        chatTitleTv.setText(chatName != null ? chatName : "Chat");

        // Setup messages RecyclerView
        messages = new ArrayList<>();
        allMessages = new ArrayList<>();
        messageAdapter = new ChatMessageAdapter(messages, chatManager.getCurrentUserId());
        messagesRv.setLayoutManager(new LinearLayoutManager(this));
        messagesRv.setAdapter(messageAdapter);

        // Send button listener
        sendBtn.setOnClickListener(v -> sendMessage());

        // Back button listener
        backBtn.setOnClickListener(v -> finish());

        // Info button listener
        infoBtn.setOnClickListener(v -> openChatInfo());

        // Search toggle button listener
        searchToggleBtn.setOnClickListener(v -> toggleSearch());

        // Search button listener
        searchBtn.setOnClickListener(v -> performSearch());

        // Clear search button listener
        clearSearchBtn.setOnClickListener(v -> clearSearch());

        // Observe incoming messages using LiveData
        messageObserver = messageEvent -> {
            if (messageEvent != null && chatId.equals(messageEvent.chatId)) {
                runOnUiThread(() -> {
                    // Parse message format: {groupId, senderId, content, timestamp}
                    int senderId = 0;
                    String content = "";
                    long timestamp = System.currentTimeMillis();
                    String groupId = chatId;

                    try {
                        JSONObject json = new JSONObject(messageEvent.message);
                        groupId = json.optString("groupId", chatId);
                        senderId = json.optInt("senderId", 0);
                        content = json.optString("content", "");
                        timestamp = json.optLong("timestamp", System.currentTimeMillis());

                        Log.d(TAG, "Parsed message - groupId: " + groupId + ", senderId: " + senderId + ", content: " + content);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing message JSON: " + e.getMessage());
                        // Fallback: treat entire message as content
                        content = messageEvent.message;
                    }

                    boolean isSentByMe = (senderId == chatManager.getCurrentUserId());

                    ChatMessage chatMessage = new ChatMessage(
                            groupId,
                            senderId,
                            content,
                            timestamp,
                            isSentByMe
                    );

                    messages.add(chatMessage);
                    allMessages.add(chatMessage);
                    messageAdapter.notifyItemInserted(messages.size() - 1);
                    messagesRv.scrollToPosition(messages.size() - 1);

                    chatManager.updateLastMessage(chatId, content);
                });
            }
        };

        chatManager.getMessageEvent().observe(this, messageObserver);

        // Connect to WebSocket for this chat
        connectWebSocket();

        Log.d(TAG, "ChatActivity initialized for chatId: " + chatId);
    }

    private void connectWebSocket() {
        // Get the ChatRoom to retrieve the WebSocket URL
        ChatRoom chatRoom = chatManager.getChatRoom(chatId);
        if (chatRoom != null && chatRoom.getWebSocketUrl() != null) {
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.setAction("CONNECT");
            serviceIntent.putExtra("key", chatId);
            serviceIntent.putExtra("url", chatRoom.getWebSocketUrl());
            startService(serviceIntent);
            Log.d(TAG, "Connected to WebSocket for chatId: " + chatId);
        } else {
            Log.w(TAG, "No WebSocket URL found for chatId: " + chatId);
        }
    }

    private void sendMessage() {
        String messageContent = messageEt.getText().toString().trim();

        if (messageContent.isEmpty()) {
            return;
        }

        // Build message in format: {groupId, senderId, content, timestamp}
        try {
            JSONObject messageJson = new JSONObject();
            messageJson.put("groupId", chatId);
            messageJson.put("senderId", chatManager.getCurrentUserId());
            messageJson.put("content", messageContent);
            messageJson.put("timestamp", System.currentTimeMillis());

            // Send message through ChatManager LiveData
            chatManager.sendMessage(chatId, messageJson.toString());

            Log.d(TAG, "Message sent for chatId " + chatId + ": " + messageJson.toString());

            // Add message to local list (optimistic update)
            ChatMessage chatMessage = new ChatMessage(
                    chatId,
                    chatManager.getCurrentUserId(),
                    messageContent,
                    System.currentTimeMillis(),
                    true
            );
            messages.add(chatMessage);
            allMessages.add(chatMessage);
            messageAdapter.notifyItemInserted(messages.size() - 1);
            messagesRv.scrollToPosition(messages.size() - 1);

            messageEt.setText("");

        } catch (JSONException e) {
            Log.e(TAG, "Error creating message JSON: " + e.getMessage());
            Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
        }
    }

    private void openChatInfo() {
        Intent intent = new Intent(this, ChatInfoActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("chatName", chatName);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("chatLeft", false)) {
                // User left the chat, close this activity too
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Disconnect from WebSocket when leaving the chat screen
        disconnectWebSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure WebSocket is disconnected when activity is destroyed
        disconnectWebSocket();
    }

    private void disconnectWebSocket() {
        if (chatId != null) {
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.setAction("DISCONNECT");
            serviceIntent.putExtra("key", chatId);
            startService(serviceIntent);
            Log.d(TAG, "Disconnected from WebSocket for chatId: " + chatId);
        }
    }

    private void toggleSearch() {
        if (searchBarLayout.getVisibility() == View.VISIBLE) {
            searchBarLayout.setVisibility(View.GONE);
            clearSearch();
        } else {
            searchBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void performSearch() {
        String searchQuery = searchEt.getText().toString().trim();

        if (searchQuery.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save all messages before searching
        if (!isSearching) {
            allMessages.clear();
            allMessages.addAll(messages);
        }

        isSearching = true;

        // Build URL: GET /chat/groups/{id}/messages?search={string}
        String url = APP_API_URL + "chat/groups/" + chatId + "/messages?search=" + searchQuery;

        Log.d(TAG, "Searching messages with query: " + searchQuery);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                Log.d(TAG, "Search response received: " + response.length() + " messages");
                List<ChatMessage> searchResults = new ArrayList<>();

                try {
                    // Parse search results: [{chatId, senderId, content, timestamp, messageType}, ...]
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject msgObj = response.getJSONObject(i);

                        String msgChatId = msgObj.optString("chatId", chatId);
                        int senderId = msgObj.optInt("senderId", 0);
                        String content = msgObj.optString("content", "");
                        long timestamp = msgObj.optLong("timestamp", System.currentTimeMillis());

                        boolean isSentByCurrentUser = (senderId == chatManager.getCurrentUserId());

                        ChatMessage chatMessage = new ChatMessage(
                            msgChatId,
                            senderId,
                            content,
                            timestamp,
                            isSentByCurrentUser
                        );

                        searchResults.add(chatMessage);
                    }

                    // Update UI with search results
                    messages.clear();
                    messages.addAll(searchResults);
                    messageAdapter.notifyDataSetChanged();

                    Toast.makeText(this, "Found " + searchResults.size() + " messages", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing search results", e);
                    Toast.makeText(this, "Error parsing search results", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e(TAG, "Search error: " + error.toString());
                Toast.makeText(this, "Search failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void clearSearch() {
        // Restore all messages
        if (isSearching && allMessages != null) {
            messages.clear();
            messages.addAll(allMessages);
            messageAdapter.notifyDataSetChanged();
            isSearching = false;
        }

        // Clear search input
        searchEt.setText("");

        Toast.makeText(this, "Search cleared", Toast.LENGTH_SHORT).show();
    }
}