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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class StudentChatFragment extends Fragment {

    private RecyclerView chatListRecyclerView;
    private ChatListAdapter chatAdapter;
    private ChatViewModel vm;
    private Button createChatButton;

    private final List<Chat> chatList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_student_chat, container, false);

        chatListRecyclerView = rootView.findViewById(R.id.chatListRecyclerView);
        createChatButton = rootView.findViewById(R.id.createChatBtn);

        // RecyclerView setup
        chatAdapter = new ChatListAdapter(getContext(), chatList, chat -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("chatKey", chat.getChatId()); // pass the chat ID
            startActivity(intent);
        });
        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatListRecyclerView.setAdapter(chatAdapter);

        // ViewModel
        vm = new ViewModelProvider(this).get(ChatViewModel.class);
        vm.getChats().observe(getViewLifecycleOwner(), chats -> {
            chatList.clear();
            chatList.addAll(chats);
            chatAdapter.notifyDataSetChanged();
        });

        // New chat button
        createChatButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateChatActivity.class);
            startActivity(intent);
        });

        return rootView;
    }
}
