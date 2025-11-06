package com.example.myapplication.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatRoom> chatRooms;
    private Context context;

    public ChatListAdapter(Context context, List<ChatRoom> chatRooms) {
        this.context = context;
        this.chatRooms = chatRooms;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_room, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);
        holder.chatNameTv.setText(chatRoom.getChatName());
        holder.lastMessageTv.setText(chatRoom.getLastMessage().isEmpty()
                ? "No messages yet"
                : chatRoom.getLastMessage());

        // Format timestamp
        if (chatRoom.getLastMessageTimestamp() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            holder.timestampTv.setText(sdf.format(new Date(chatRoom.getLastMessageTimestamp())));
        } else {
            holder.timestampTv.setText("");
        }

        // Show number of professionals
        int profCount = chatRoom.getProfessionalIds().size();
        holder.profCountTv.setText(profCount + " professional" + (profCount != 1 ? "s" : ""));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatId", chatRoom.getChatId());
            intent.putExtra("chatName", chatRoom.getChatName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public void updateChatRooms(List<ChatRoom> newChatRooms) {
        this.chatRooms = newChatRooms;
        notifyDataSetChanged();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatNameTv, lastMessageTv, timestampTv, profCountTv;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatNameTv = itemView.findViewById(R.id.chatNameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
            timestampTv = itemView.findViewById(R.id.timestampTv);
            profCountTv = itemView.findViewById(R.id.profCountTv);
        }
    }
}