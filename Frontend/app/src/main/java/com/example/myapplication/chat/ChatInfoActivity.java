package com.example.myapplication.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ChatInfoActivity extends AppCompatActivity implements ChatMembersAdapter.OnMemberActionListener {

    private TextView chatNameTv;
    private RecyclerView membersRv;
    private Button backBtn, addMemberBtn, leaveChatBtn;
    private ChatMembersAdapter membersAdapter;
    private List<ChatMember> members;
    private ChatManager chatManager;
    private String chatId;
    private String chatName;
    private ChatRoom chatRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        chatManager = ChatManager.getInstance();

        // Get chat info from intent
        chatId = getIntent().getStringExtra("chatId");
        chatName = getIntent().getStringExtra("chatName");

        if (chatId == null) {
            Toast.makeText(this, "Error loading chat info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatRoom = chatManager.getChatRoom(chatId);

        // Initialize views
        chatNameTv = findViewById(R.id.chatNameTv);
        membersRv = findViewById(R.id.membersRv);
        backBtn = findViewById(R.id.backBtn);
        addMemberBtn = findViewById(R.id.addMemberBtn);
        leaveChatBtn = findViewById(R.id.leaveChatBtn);

        chatNameTv.setText(chatName != null ? chatName : "Chat");

        // Setup members list
        members = new ArrayList<>();
        loadMembers();

        membersRv.setLayoutManager(new LinearLayoutManager(this));
        membersAdapter = new ChatMembersAdapter(members, this);
        membersRv.setAdapter(membersAdapter);

        // Back button
        backBtn.setOnClickListener(v -> finish());

        // Add member button
        addMemberBtn.setOnClickListener(v -> openAddMemberDialog());

        // Leave chat button
        leaveChatBtn.setOnClickListener(v -> showLeaveChatConfirmation());
    }

    private void loadMembers() {
        members.clear();

        if (chatRoom != null && chatRoom.getProfessionalIds() != null) {
            // Add professionals as members
            for (Integer profId : chatRoom.getProfessionalIds()) {
                if (profId == null) continue;

                // TODO: Fetch professional names from backend
                // For now, using placeholder names
                String profName = "Professional " + profId;
                if (profId == 71) {
                    profName = "Garrett Thompson";
                }

                // Can remove if not yourself
                boolean canRemove = (chatManager.getCurrentUserId() != profId);
                members.add(new ChatMember(profId, profName, "Professional", canRemove));
            }
        }

        // TODO: Add student members when backend supports it
        // For now, we don't have a list of student members in the ChatRoom

        if (membersAdapter != null) {
            membersAdapter.updateMembers(members);
        }
    }

    private void openAddMemberDialog() {
        Intent intent = new Intent(this, AddMemberActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("chatName", chatName);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload members in case they were added
        chatRoom = chatManager.getChatRoom(chatId);
        loadMembers();
    }

    @Override
    public void onRemoveMember(ChatMember member) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Member")
                .setMessage("Are you sure you want to remove " + member.getName() + " from this chat?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    // Remove from chat room
                    if (chatRoom != null) {
                        List<Integer> profIds = chatRoom.getProfessionalIds();
                        profIds.remove(Integer.valueOf(member.getUserId()));

                        Toast.makeText(this, member.getName() + " removed from chat", Toast.LENGTH_SHORT).show();
                        loadMembers();

                        // TODO: Send remove member request to backend when available
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLeaveChatConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Leave Chat")
                .setMessage("Are you sure you want to leave this chat?")
                .setPositiveButton("Leave", (dialog, which) -> {
                    leaveChat();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void leaveChat() {
        // Remove current user from chat
        int currentUserId = chatManager.getCurrentUserId();

        android.util.Log.d("ChatInfoActivity", "Leaving chat: " + chatId + " as user: " + currentUserId);

        if (chatRoom != null) {
            boolean isCreator = (chatRoom.getCreatorId() == currentUserId);
            android.util.Log.d("ChatInfoActivity", "Is creator: " + isCreator + " (creatorId=" + chatRoom.getCreatorId() + ")");

            if (isCreator) {
                // Student/creator leaving - delete the entire chat from backend
                android.util.Log.d("ChatInfoActivity", "Creator leaving - deleting entire chat");

                chatManager.deleteChatGroup(this, chatId, new ChatManager.ChatGroupDeleteCallback() {
                    @Override
                    public void onSuccess() {
                        android.util.Log.d("ChatInfoActivity", "Chat deleted from backend successfully");
                        runOnUiThread(() -> {
                            Toast.makeText(ChatInfoActivity.this, "Chat deleted", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.putExtra("chatLeft", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        android.util.Log.e("ChatInfoActivity", "Failed to delete chat: " + errorMessage);
                        // Still remove locally even if backend fails
                        chatManager.removeChatRoom(chatId);
                        runOnUiThread(() -> {
                            Toast.makeText(ChatInfoActivity.this, "You left the chat (offline)", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.putExtra("chatLeft", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        });
                    }
                });
            } else {
                // Professional leaving - update the chat to remove them
                List<Integer> profIds = new ArrayList<>(chatRoom.getProfessionalIds());
                List<Integer> studentIds = new ArrayList<>(chatRoom.getStudentIds());

                android.util.Log.d("ChatInfoActivity", "Professional IDs before removal: " + profIds.toString());
                profIds.remove(Integer.valueOf(currentUserId));
                android.util.Log.d("ChatInfoActivity", "Professional IDs after removal: " + profIds.toString());

                if (profIds.isEmpty()) {
                    // If no professionals left, delete the entire chat
                    android.util.Log.d("ChatInfoActivity", "No professionals left - deleting chat");

                    chatManager.deleteChatGroup(this, chatId, new ChatManager.ChatGroupDeleteCallback() {
                        @Override
                        public void onSuccess() {
                            android.util.Log.d("ChatInfoActivity", "Chat deleted from backend (no professionals left)");
                            runOnUiThread(() -> {
                                Toast.makeText(ChatInfoActivity.this, "Chat deleted (no professionals left)", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.putExtra("chatLeft", true);
                                setResult(RESULT_OK, intent);
                                finish();
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            android.util.Log.e("ChatInfoActivity", "Failed to delete chat: " + errorMessage);
                            chatManager.removeChatRoom(chatId);
                            runOnUiThread(() -> {
                                Toast.makeText(ChatInfoActivity.this, "You left the chat (offline)", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.putExtra("chatLeft", true);
                                setResult(RESULT_OK, intent);
                                finish();
                            });
                        }
                    });
                } else {
                    // Update the chat group on backend to remove this professional
                    chatManager.updateChatGroup(this, chatId, profIds, studentIds, chatRoom.getChatName(),
                        new ChatManager.ChatGroupUpdateCallback() {
                            @Override
                            public void onSuccess(String response) {
                                android.util.Log.d("ChatInfoActivity", "Successfully removed from chat on backend");
                                // Remove from local cache
                                chatManager.removeChatRoom(chatId);

                                runOnUiThread(() -> {
                                    Toast.makeText(ChatInfoActivity.this, "You left the chat", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.putExtra("chatLeft", true);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                });
                            }

                            @Override
                            public void onError(String errorMessage) {
                                android.util.Log.e("ChatInfoActivity", "Failed to update chat: " + errorMessage);
                                // Still remove locally even if backend fails
                                chatManager.removeChatRoom(chatId);

                                runOnUiThread(() -> {
                                    Toast.makeText(ChatInfoActivity.this, "You left the chat (offline)", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.putExtra("chatLeft", true);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                });
                            }
                        });
                }
            }

            android.util.Log.d("ChatInfoActivity", "Total chats in manager: " + chatManager.getAllChatRooms().size());
        }
    }
}
