package com.example.myapplication.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private RecyclerView messagesRv;
    private EditText messageEt;
    private Button sendBtn, backBtn, infoBtn;
    private TextView chatTitleTv;

    private String chatId;
    private String chatName;
    private ChatMessageAdapter messageAdapter;
    private List<ChatMessage> messages;
    private ChatManager chatManager;
    private Observer<ChatManager.MessageEvent> messageObserver;

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
        sendBtn = findViewById(R.id.sendBtn);
        backBtn = findViewById(R.id.backBtn);
        infoBtn = findViewById(R.id.infoBtn);
        chatTitleTv = findViewById(R.id.chatTitleTv);

        chatTitleTv.setText(chatName != null ? chatName : "Chat");

        // Setup messages RecyclerView
        messages = new ArrayList<>();
        messageAdapter = new ChatMessageAdapter(messages, chatManager.getCurrentUserId());
        messagesRv.setLayoutManager(new LinearLayoutManager(this));
        messagesRv.setAdapter(messageAdapter);

        // Send button listener
        sendBtn.setOnClickListener(v -> sendMessage());

        // Back button listener
        backBtn.setOnClickListener(v -> finish());

        // Info button listener
        infoBtn.setOnClickListener(v -> openChatInfo());

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
}