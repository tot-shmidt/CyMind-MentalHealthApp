package com.example.myapplication.chat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;
    private int currentUserId;

    public ChatMessageAdapter(List<ChatMessage> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        holder.messageTv.setText(message.getContent());

        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.timestampTv.setText(sdf.format(new Date(message.getTimestamp())));

        // Set sender name and alignment
        if (message.isSentByCurrentUser()) {
            holder.senderTv.setVisibility(View.GONE);
            holder.messageContainer.setGravity(Gravity.END);
            holder.messageTv.setBackgroundResource(R.drawable.bg_message_sent);
        } else {
            holder.senderTv.setVisibility(View.VISIBLE);
            holder.senderTv.setText(message.getSenderId());
            holder.messageContainer.setGravity(Gravity.START);
            holder.messageTv.setBackgroundResource(R.drawable.bg_message_sent);
            // update with message received
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTv, timestampTv, senderTv;
        LinearLayout messageContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTv = itemView.findViewById(R.id.messageTv);
            timestampTv = itemView.findViewById(R.id.timestampTv);
            senderTv = itemView.findViewById(R.id.senderTv);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }
    }
}