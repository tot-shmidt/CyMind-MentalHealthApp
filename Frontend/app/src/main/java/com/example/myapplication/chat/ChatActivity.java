package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.chat.ChatViewModel;
import com.example.myapplication.chat.Message;
import com.example.myapplication.chat.MessageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Button sendBtn, backMainBtn;
    private EditText msgEtx;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messages = new ArrayList<>();

    private ChatViewModel vm;
    private static final String TAG = "ChatActivity";

    // unique key for this chat (you might get this from an Intent)
    private final String chatKey = "chat1";
    private final String chatUrl = "ws://10.0.2.2:8080/chat/1/testUser"; // example mock URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d(TAG, "onCreate: ChatActivity initialized.");

        // --- Initialize UI elements ---
        sendBtn = findViewById(R.id.sendBtn);
        backMainBtn = findViewById(R.id.backMainBtn);
        msgEtx = findViewById(R.id.msgEdit);
        messageRecyclerView = findViewById(R.id.messageRecyclerView);

        // TODO setup this intent
        int currentUserId = getIntent().getIntExtra("userID", 0);

        // --- Setup RecyclerView and adapter ---
        messageAdapter = new MessageAdapter(messages, this, currentUserId);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(messageAdapter);

        // --- Initialize ViewModel ---
        vm = new ViewModelProvider(this).get(ChatViewModel.class);

        // --- Observe messages from LiveData ---
        vm.getMessages().observe(this, allMessages -> {
            List<Message> chatMsgs = new ArrayList<>();
            for (Message msg : allMessages) {
                if (chatKey.equals(msg.getChatKey())) {
                    chatMsgs.add(msg);
                }
            }

            messages.clear();
            messages.addAll(chatMsgs);
            messageAdapter.notifyDataSetChanged();

            // Scroll to bottom
            messageRecyclerView.scrollToPosition(messages.size() - 1);
        });
        // --- Connect to WebSocket ---
        vm.connect(chatKey, chatUrl);

        // --- Send button listener ---
        sendBtn.setOnClickListener(v -> {
            String msg = msgEtx.getText().toString().trim();
            if (msg.isEmpty()) {
                Log.w(TAG, "sendBtn clicked but message was empty.");
                return;
            }
            vm.send(chatKey, msg);
            msgEtx.setText("");
        });

        // --- Back button listener ---
        backMainBtn.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vm.disconnect(chatKey);
    }
}
