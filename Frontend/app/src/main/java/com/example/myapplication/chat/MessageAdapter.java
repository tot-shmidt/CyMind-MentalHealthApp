package com.example.myapplication.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

/**
 * Adapter to display chat messages in a RecyclerView.
 * Shows messages differently depending on whether they are sent by the current user.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private final Context context;
    private final int currentUserId; // ID of the logged-in user

    public MessageAdapter(List<Message> messages, Context context, int currentUserId) {
        this.messages = messages;
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the same layout; we'll adjust alignment in onBindViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getContent());
        holder.senderText.setText(message.getSenderName());

        // Align message based on sender
        if (message.getSenderId().equals(String.valueOf(currentUserId))) {
            holder.itemView.setBackgroundResource(R.drawable.bg_message_right);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_message_left);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView senderText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageTextView);
            senderText = itemView.findViewById(R.id.senderTextView);
        }
    }
}
