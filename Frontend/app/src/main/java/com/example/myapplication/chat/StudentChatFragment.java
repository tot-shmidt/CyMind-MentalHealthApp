package com.example.myapplication.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentChatFragment extends Fragment {

    private RecyclerView chatListRv;
    private Button createChatBtn;
    private ChatListAdapter adapter;
    private ChatManager chatManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_chat, container, false);

        chatManager = ChatManager.getInstance();

        // Initialize UI elements
        chatListRv = view.findViewById(R.id.chatListRv);
        createChatBtn = view.findViewById(R.id.createChatBtn);

        // Setup RecyclerView
        chatListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(getContext(), new ArrayList<>());
        chatListRv.setAdapter(adapter);

        // Load existing chats
        loadChatRooms();

        // Create chat button listener
        createChatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateChatActivity.class);
            startActivity(intent);
        });

        // Observe incoming messages to update last message preview
        chatManager.getMessageEvent().observe(getViewLifecycleOwner(), messageEvent -> {
            if (messageEvent != null) {
                chatManager.updateLastMessage(messageEvent.chatId, messageEvent.message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::loadChatRooms);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        android.util.Log.d("StudentChatFragment", "onResume called - refreshing chat list");
        // Refresh chat list when returning to fragment
        loadChatRooms();
    }

    private void loadChatRooms() {
        List<ChatRoom> chatRooms = chatManager.getAllChatRooms();

        // Debug logging
        android.util.Log.d("StudentChatFragment", "Student ID: " + chatManager.getCurrentUserId());
        android.util.Log.d("StudentChatFragment", "Total chats: " + chatRooms.size());
        for (ChatRoom room : chatRooms) {
            android.util.Log.d("StudentChatFragment", "Chat: " + room.getChatName() +
                " - Professional IDs: " + room.getProfessionalIds().toString());
        }

        adapter.updateChatRooms(chatRooms);

        // TODO: Uncomment when backend is ready to fetch chats from server
        // fetchChatsFromBackend();
    }

    /**
     * Example method to fetch chats from backend.
     * This will retrieve all messages for the current user and organize them by group.
     */
    private void fetchChatsFromBackend() {
        int userId = chatManager.getCurrentUserId();

        chatManager.fetchChatsFromBackend(getContext(), userId, new ChatManager.ChatFetchCallback() {
            @Override
            public void onSuccess(List<ChatMessage> allMessages, Map<String, List<ChatMessage>> messagesByGroup) {
                // Process the fetched messages
                // messagesByGroup contains messages organized by groupId
                // You can use this to populate chat rooms or display recent messages

                // Example: Update last message for each group
                for (Map.Entry<String, List<ChatMessage>> entry : messagesByGroup.entrySet()) {
                    String groupId = entry.getKey();
                    List<ChatMessage> groupMessages = entry.getValue();

                    if (!groupMessages.isEmpty()) {
                        // Get the most recent message
                        ChatMessage lastMessage = groupMessages.get(groupMessages.size() - 1);
                        chatManager.updateLastMessage(groupId, lastMessage.getContent());
                    }
                }

                // Refresh the UI
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> loadChatRooms());
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error - maybe show a toast
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                        android.widget.Toast.makeText(getContext(),
                            "Failed to load chats: " + errorMessage,
                            android.widget.Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}