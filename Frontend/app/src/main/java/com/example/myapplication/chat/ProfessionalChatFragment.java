package com.example.myapplication.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfessionalChatFragment extends Fragment {

    private RecyclerView chatListRv;
    private ChatListAdapter adapter;
    private ChatManager chatManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professional_chat, container, false);

        chatManager = ChatManager.getInstance();

        // Initialize UI elements
        chatListRv = view.findViewById(R.id.chatListRv);

        // Setup RecyclerView
        chatListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(getContext(), new ArrayList<>());
        chatListRv.setAdapter(adapter);

        // Load existing chats that this professional has access to
        loadChatRooms();

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
        android.util.Log.d("ProfessionalChatFragment", "onResume called - refreshing chat list");
        // Refresh chat list when returning to fragment
        loadChatRooms();
    }

    private void loadChatRooms() {
        // Get only chats where this professional is included
        int professionalId = chatManager.getCurrentUserId();

        // Debug logging
        android.util.Log.d("ProfessionalChatFragment", "Professional ID: " + professionalId);
        android.util.Log.d("ProfessionalChatFragment", "Total chats in manager: " + chatManager.getAllChatRooms().size());

        List<ChatRoom> allChats = chatManager.getAllChatRooms();
        for (ChatRoom room : allChats) {
            android.util.Log.d("ProfessionalChatFragment", "Chat: " + room.getChatName() +
                " - Professional IDs: " + room.getProfessionalIds().toString());
        }

        List<ChatRoom> professionalChats = chatManager.getChatRoomsForProfessional(professionalId);
        android.util.Log.d("ProfessionalChatFragment", "Filtered chats for professional: " + professionalChats.size());

        adapter.updateChatRooms(professionalChats);

        // TODO: Uncomment when backend is ready to fetch chats from server
        // fetchChatsFromBackend();
    }

    /**
     * Example method to fetch chats from backend for professionals.
     * This will retrieve all messages for the current professional and organize them by group.
     * Professionals will only see chats (groups) where they have been added by students.
     */
    private void fetchChatsFromBackend() {
        int professionalId = chatManager.getCurrentUserId();

        chatManager.fetchChatsFromBackend(getContext(), professionalId, new ChatManager.ChatFetchCallback() {
            @Override
            public void onSuccess(List<ChatMessage> allMessages, Map<String, List<ChatMessage>> messagesByGroup) {
                // Process the fetched messages
                // messagesByGroup contains messages organized by groupId
                // Filter to only show groups where this professional is a member

                // Example: Update last message for each group
                for (Map.Entry<String, List<ChatMessage>> entry : messagesByGroup.entrySet()) {
                    String groupId = entry.getKey();
                    List<ChatMessage> groupMessages = entry.getValue();

                    // Check if professional has access to this group
                    ChatRoom room = chatManager.getChatRoom(groupId);
                    if (room != null && room.getProfessionalIds().contains(professionalId)) {
                        if (!groupMessages.isEmpty()) {
                            // Get the most recent message
                            ChatMessage lastMessage = groupMessages.get(groupMessages.size() - 1);
                            chatManager.updateLastMessage(groupId, lastMessage.getContent());
                        }
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
