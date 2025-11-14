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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;
    private int currentUserId;
    private OnMessageActionListener actionListener;

    public interface OnMessageActionListener {
        void onEditMessage(ChatMessage message, int position);
        void onDeleteMessage(ChatMessage message, int position);
    }

    public ChatMessageAdapter() {
        messages = null;
        currentUserId = 0;
        actionListener = null;
    }

    public ChatMessageAdapter(List<ChatMessage> messages, int currentUserId, OnMessageActionListener actionListener) {
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.actionListener = actionListener;
    }

    // Backward compatible constructor
    public ChatMessageAdapter(List<ChatMessage> messages, int currentUserId) {
        this(messages, currentUserId, null);
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

        // Format timestamp using LocalDateTime
        LocalDateTime timestamp = message.getTimestamp();
        if (timestamp != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            holder.timestampTv.setText(timestamp.format(formatter));
        } else {
            holder.timestampTv.setText("");
        }

        // Set sender name and alignment
        if (message.isSentByCurrentUser()) {
            holder.senderTv.setVisibility(View.GONE);
            holder.messageContainer.setGravity(Gravity.END);
            holder.messageTv.setBackgroundResource(R.drawable.bg_message_sent);
        } else {
            holder.senderTv.setVisibility(View.VISIBLE);
            // Display sender name if available, otherwise fall back to "User [id]"
            if (message.getSenderName() != null && !message.getSenderName().isEmpty()) {
                holder.senderTv.setText(message.getSenderName());
            } else {
                holder.senderTv.setText("User " + message.getSenderId());
            }
            holder.messageContainer.setGravity(Gravity.START);
            holder.messageTv.setBackgroundResource(R.drawable.bg_message_sent);
            // update with message received
        }

        // Long-press listener for edit/delete (only for current user's messages)
        if (message.isSentByCurrentUser() && actionListener != null) {
            holder.messageTv.setOnLongClickListener(v -> {
                showMessageOptions(v, message, position);
                return true;
            });
        } else {
            holder.messageTv.setOnLongClickListener(null);
        }
    }

    private void showMessageOptions(View view, ChatMessage message, int position) {
        // Create options dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());
        builder.setTitle("Message Options");

        String[] options = {"Edit", "Delete"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Edit
                    if (actionListener != null) {
                        actionListener.onEditMessage(message, position);
                    }
                    break;
                case 1: // Delete
                    if (actionListener != null) {
                        actionListener.onDeleteMessage(message, position);
                    }
                    break;
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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