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
                    // Parse message (adjust based on your backend format)
                    int senderId = 0; // Default
                    String content = messageEvent.message;

                    if (messageEvent.message.contains(":")) {
                        String[] parts = messageEvent.message.split(":", 2);
                        try {
                            senderId = Integer.parseInt(parts[0]);
                            content = parts.length > 1 ? parts[1] : "";
                        } catch (NumberFormatException e) {
                            // If parsing fails, use default
                            Log.w(TAG, "Failed to parse senderId from message");
                        }
                    }

                    boolean isSentByMe = (senderId == chatManager.getCurrentUserId());

                    ChatMessage chatMessage = new ChatMessage(
                            senderId,
                            content,
                            System.currentTimeMillis(),
                            isSentByMe
                    );

                    messages.add(chatMessage);
                    messageAdapter.notifyItemInserted(messages.size() - 1);
                    messagesRv.scrollToPosition(messages.size() - 1);

                    chatManager.updateLastMessage(chatId, content);

                    /* TODO: Uncomment when backend implements JSON message format
                    // Expected format: {"groupId": "string", "content": "string", "senderUserId": int}
                    int senderId = 0;
                    String content = messageEvent.message;
                    String groupId = null;

                    try {
                        org.json.JSONObject json = new org.json.JSONObject(messageEvent.message);
                        groupId = json.optString("groupId", null);
                        content = json.optString("content", messageEvent.message);
                        senderId = json.optInt("senderUserId", 0);
                    } catch (org.json.JSONException e) {
                        // Not JSON, try old format (senderId:message)
                        if (messageEvent.message.contains(":")) {
                            String[] parts = messageEvent.message.split(":", 2);
                            try {
                                senderId = Integer.parseInt(parts[0]);
                                content = parts.length > 1 ? parts[1] : "";
                            } catch (NumberFormatException ex) {
                                Log.w(TAG, "Failed to parse senderId from message");
                            }
                        }
                    }

                    boolean isSentByMe = (senderId == chatManager.getCurrentUserId());

                    ChatMessage chatMessage;
                    if (groupId != null) {
                        chatMessage = new ChatMessage(
                                groupId,
                                senderId,
                                content,
                                System.currentTimeMillis(),
                                isSentByMe
                        );
                    } else {
                        chatMessage = new ChatMessage(
                                senderId,
                                content,
                                System.currentTimeMillis(),
                                isSentByMe
                        );
                    }

                    messages.add(chatMessage);
                    messageAdapter.notifyItemInserted(messages.size() - 1);
                    messagesRv.scrollToPosition(messages.size() - 1);

                    chatManager.updateLastMessage(chatId, content);
                    */
                });
            }
        };

        chatManager.getMessageEvent().observe(this, messageObserver);

        Log.d(TAG, "ChatActivity initialized for chatId: " + chatId);
    }

    private void sendMessage() {
        String message = messageEt.getText().toString().trim();

        if (message.isEmpty()) {
            return;
        }

        // Send message through ChatManager LiveData
        chatManager.sendMessage(chatId, message);

        Log.d(TAG, "Message sent for chatId " + chatId + ": " + message);

        // Add message to local list (optimistic update)
        ChatMessage chatMessage = new ChatMessage(
                chatManager.getCurrentUserId(),
                message,
                System.currentTimeMillis(),
                true
        );
        messages.add(chatMessage);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        messagesRv.scrollToPosition(messages.size() - 1);

        messageEt.setText("");
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

        // Build URL with search parameter
        String url = APP_API_URL + "chat/group/messages?search=" + searchQuery;

        Log.d(TAG, "Searching messages with query: " + searchQuery);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                Log.d(TAG, "Search response received: " + response.length() + " messages");
                List<ChatMessage> searchResults = new ArrayList<>();

                try {
                    // Parse search results
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject msgObj = response.getJSONObject(i);

                        String groupId = msgObj.optString("groupId", null);
                        String content = msgObj.optString("content", "");
                        int senderUserId = msgObj.optInt("senderUserId", 0);

                        boolean isSentByCurrentUser = (senderUserId == chatManager.getCurrentUserId());

                        ChatMessage chatMessage = new ChatMessage(
                            groupId,
                            senderUserId,
                            content,
                            System.currentTimeMillis(),
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