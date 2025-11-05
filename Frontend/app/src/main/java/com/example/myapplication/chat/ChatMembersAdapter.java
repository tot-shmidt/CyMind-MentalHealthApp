package com.example.myapplication.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ChatMembersAdapter extends RecyclerView.Adapter<ChatMembersAdapter.MemberViewHolder> {

    private List<ChatMember> members;
    private OnMemberActionListener listener;

    public interface OnMemberActionListener {
        void onRemoveMember(ChatMember member);
    }

    public ChatMembersAdapter(List<ChatMember> members, OnMemberActionListener listener) {
        this.members = members;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        ChatMember member = members.get(position);
        holder.memberNameTv.setText(member.getName());
        holder.memberTypeTv.setText(member.getType());

        if (member.isRemovable()) {
            holder.removeMemberBtn.setVisibility(View.VISIBLE);
            holder.removeMemberBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveMember(member);
                }
            });
        } else {
            holder.removeMemberBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateMembers(List<ChatMember> newMembers) {
        this.members = newMembers;
        notifyDataSetChanged();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView memberNameTv, memberTypeTv;
        Button removeMemberBtn;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberNameTv = itemView.findViewById(R.id.memberNameTv);
            memberTypeTv = itemView.findViewById(R.id.memberTypeTv);
            removeMemberBtn = itemView.findViewById(R.id.removeMemberBtn);
        }
    }
}
