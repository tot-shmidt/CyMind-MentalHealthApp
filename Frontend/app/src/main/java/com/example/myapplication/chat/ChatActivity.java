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

import java.time.LocalDateTime;
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
    private ChatMessage editingMessage = null; // Track message being edited

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
        messageAdapter = new ChatMessageAdapter(messages, chatManager.getCurrentUserId(), new ChatMessageAdapter.OnMessageActionListener() {
            @Override
            public void onEditMessage(ChatMessage message, int position) {
                startEditingMessage(message);
            }

            @Override
            public void onDeleteMessage(ChatMessage message, int position) {
                deleteMessage(message);
            }
        });
        messagesRv.setLayoutManager(new LinearLayoutManager(this));
        messagesRv.setAdapter(messageAdapter);

        // Send button listener
        sendBtn.setOnClickListener(v -> sendMessage());

        // Back button listener
        backBtn.setOnClickListener(v -> {
            if (editingMessage != null) {
                // Cancel edit mode if active
                cancelEdit();
            } else {
                finish();
            }
        });

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
            Log.d(TAG, "MessageObserver called - messageEvent: " + (messageEvent != null ? "not null" : "null") +
                    ", eventChatId: " + (messageEvent != null ? messageEvent.chatId : "null") +
                    ", thisChatId: " + chatId);

            if (messageEvent != null && chatId.equals(messageEvent.chatId)) {
                Log.d(TAG, "Processing message for chatId: " + chatId);
                runOnUiThread(() -> {
                    // Parse message format: {messageId, senderId, name, content, timestamp, messageType}
                    String messageId = null;
                    int senderId = 0;
                    String senderName = null;
                    String content = "";
                    LocalDateTime timestamp = LocalDateTime.now();
                    String messageType = "MESSAGE";

                    try {
                        JSONObject json = new JSONObject(messageEvent.message);
                        messageId = json.optString("messageId", null);
                        senderId = json.optInt("senderId", 0);
                        senderName = json.optString("name", null);
                        content = json.optString("content", "");

                        // Parse timestamp as ISO string and convert to LocalDateTime
                        String timestampStr = json.optString("timestamp", null);
                        if (timestampStr != null && !timestampStr.isEmpty()) {
                            timestamp = LocalDateTime.parse(timestampStr);
                        }

                        messageType = json.optString("messageType", "MESSAGE");

                        Log.d(TAG, "Parsed message - messageId: " + messageId + ", senderId: " + senderId +
                                   ", senderName: " + senderName + ", content: " + content +
                                   ", timestamp: " + timestamp + ", messageType: " + messageType);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing message JSON: " + e.getMessage());
                        // Fallback: treat entire message as content
                        content = messageEvent.message;
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing timestamp: " + e.getMessage());
                    }

                    boolean isSentByMe = (senderId == chatManager.getCurrentUserId());

                    // Handle different message types
                    switch (messageType) {
                        case "MESSAGE":
                            // Regular message - add to list
                            ChatMessage chatMessage = new ChatMessage(
                                    messageId,
                                    chatId,
                                    senderId,
                                    senderName,
                                    content,
                                    timestamp.toString(),
                                    messageType,
                                    isSentByMe
                            );
                            messages.add(chatMessage);
                            allMessages.add(chatMessage);
                            Log.d(TAG, "Added message to list. Total messages now: " + messages.size());
                            messageAdapter.notifyItemInserted(messages.size() - 1);
                            messagesRv.scrollToPosition(messages.size() - 1);
                            chatManager.updateLastMessage(chatId, content);
                            break;

                        case "EDIT":
                            // Edited message - find and update existing message
                            if (messageId != null) {
                                for (int i = 0; i < messages.size(); i++) {
                                    if (messageId.equals(messages.get(i).getMessageId())) {
                                        ChatMessage updatedMessage = new ChatMessage(
                                                messageId,
                                                chatId,
                                                senderId,
                                                senderName,
                                                content,
                                                LocalDateTime.now().toString(),
                                                messageType,
                                                isSentByMe
                                        );
                                        messages.set(i, updatedMessage);
                                        allMessages.set(i, updatedMessage);
                                        messageAdapter.notifyItemChanged(i);
                                        Log.d(TAG, "Updated message at position " + i);
                                        break;
                                    }
                                }
                            }
                            break;

                        case "DELETE":
                            // Delete message - find and remove from list
                            if (messageId != null) {
                                for (int i = 0; i < messages.size(); i++) {
                                    if (messageId.equals(messages.get(i).getMessageId())) {
                                        messages.remove(i);
                                        allMessages.remove(i);
                                        messageAdapter.notifyItemRemoved(i);
                                        Log.d(TAG, "Deleted message at position " + i);
                                        break;
                                    }
                                }
                            }
                            break;

                        default:
                            Log.w(TAG, "Unknown message type: " + messageType);
                            break;
                    }
                });
            } else {
                Log.d(TAG, "Message filtered out - chatId mismatch or null messageEvent");
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

        // Check if we're editing an existing message
        if (editingMessage != null) {
            // Send EDIT message
            try {
                JSONObject editJson = new JSONObject();
                editJson.put("messageId", editingMessage.getMessageId());
                editJson.put("senderId", chatManager.getCurrentUserId());
                editJson.put("content", messageContent);
                editJson.put("timestamp", LocalDateTime.now().toString());
                editJson.put("messageType", "EDIT");

                chatManager.sendMessage(chatId, editJson.toString());

                Log.d(TAG, "Edit message sent: " + editJson.toString());

                // Clear editing mode
                cancelEdit();

            } catch (JSONException e) {
                Log.e(TAG, "Error creating edit JSON: " + e.getMessage());
                Toast.makeText(this, "Error editing message", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Send new MESSAGE
            try {
                LocalDateTime now = LocalDateTime.now();
                JSONObject messageJson = new JSONObject();
                messageJson.put("senderId", chatManager.getCurrentUserId());
                messageJson.put("content", messageContent);
                messageJson.put("timestamp", now.toString()); // ISO-8601 format
                messageJson.put("messageType", "MESSAGE");

                // Send message through ChatManager LiveData
                chatManager.sendMessage(chatId, messageJson.toString());

                Log.d(TAG, "Message sent for chatId " + chatId + ": " + messageJson.toString());

                // Wait for server confirmation before displaying message
                messageEt.setText("");

            } catch (JSONException e) {
                Log.e(TAG, "Error creating message JSON: " + e.getMessage());
                Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
            }
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
                        String timestamp = msgObj.optString("timestamp", LocalDateTime.now().toString());

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

    private void startEditingMessage(ChatMessage message) {
        // Set editing mode
        editingMessage = message;

        // Populate the input field with the current message content
        messageEt.setText(message.getContent());
        messageEt.setSelection(message.getContent().length()); // Move cursor to end
        messageEt.requestFocus();

        // Change send button text to indicate editing
        sendBtn.setText("Update");

        Toast.makeText(this, "Editing message", Toast.LENGTH_SHORT).show();
    }

    private void cancelEdit() {
        editingMessage = null;
        messageEt.setText("");
        sendBtn.setText("Send");
    }

    private void deleteMessage(ChatMessage message) {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Send DELETE message via WebSocket
                    try {
                        JSONObject deleteJson = new JSONObject();
                        deleteJson.put("messageId", message.getMessageId());
                        deleteJson.put("senderId", chatManager.getCurrentUserId());
                        deleteJson.put("timestamp", LocalDateTime.now().toString());
                        deleteJson.put("messageType", "DELETE");

                        chatManager.sendMessage(chatId, deleteJson.toString());

                        Log.d(TAG, "Delete message sent: " + deleteJson.toString());
                        Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        Log.e(TAG, "Error creating delete JSON: " + e.getMessage());
                        Toast.makeText(this, "Error deleting message", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}