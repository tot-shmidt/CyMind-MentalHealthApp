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

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

    private final Context context;
    private final List<Message> messages;
    private final int currentUserId;

    public MessageListAdapter(Context context, List<Message> messages, int currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.messageTextView.setText(message.getContent());
        holder.senderTextView.setText(message.getSenderName());

        // Align messages left or right depending on sender
        ViewGroup.LayoutParams params = holder.messageContainer.getLayoutParams();
        if (Integer.getInteger(message.getSenderId()) == currentUserId) {
            // Your own messages align right
            holder.messageContainer.setBackgroundResource(R.drawable.bg_message_right);
        } else {
            // Others align left
            holder.messageContainer.setBackgroundResource(R.drawable.bg_message_left);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView;
        TextView messageTextView;
        View messageContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
