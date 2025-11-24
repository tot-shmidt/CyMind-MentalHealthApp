package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private List<JournalEntry> journalList;

    public JournalAdapter(List<JournalEntry> journalList) {
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journal_entry, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        JournalEntry entry = journalList.get(position);
        holder.journalIdText.setText("ID: " + entry.getId());
        holder.journalDateText.setText("Date: " + entry.getDate().substring(5));
        holder.journalContentText.setText(entry.getContent());
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public void updateData(List<JournalEntry> newJournalList) {
        this.journalList = newJournalList;
        notifyDataSetChanged();
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        TextView journalIdText;
        TextView journalDateText;
        TextView journalContentText;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            journalIdText = itemView.findViewById(R.id.journalIdText);
            journalDateText = itemView.findViewById(R.id.journalDateText);
            journalContentText = itemView.findViewById(R.id.journalContentText);
        }
    }
}
