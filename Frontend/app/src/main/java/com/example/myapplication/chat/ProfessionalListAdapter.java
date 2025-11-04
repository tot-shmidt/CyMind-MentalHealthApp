package com.example.myapplication.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ProfessionalListAdapter extends RecyclerView.Adapter<ProfessionalListAdapter.ProfessionalViewHolder> {

    private final Context context;
    private final List<User> professionals;
    private final List<User> selectedProfessionals = new ArrayList<>();

    public ProfessionalListAdapter(Context context, List<User> professionals) {
        this.context = context;
        this.professionals = professionals;
    }

    @NonNull
    @Override
    public ProfessionalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_professional, parent, false);
        return new ProfessionalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessionalViewHolder holder, int position) {
        User professional = professionals.get(position);
        holder.nameTextView.setText(professional.getFullName());

        // Prevent recycling from changing checkbox state incorrectly
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedProfessionals.contains(professional));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedProfessionals.contains(professional)) {
                    selectedProfessionals.add(professional);
                }
            } else {
                selectedProfessionals.remove(professional);
            }
        });

        // Optional: clicking the row toggles checkbox
        holder.itemView.setOnClickListener(v -> holder.checkBox.setChecked(!holder.checkBox.isChecked()));
    }

    @Override
    public int getItemCount() {
        return professionals.size();
    }

    public List<User> getSelectedProfessionals() {
        return selectedProfessionals;
    }

    static class ProfessionalViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        CheckBox checkBox;

        public ProfessionalViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.professionalNameTextView);
            checkBox = itemView.findViewById(R.id.professionalCheckBox);
        }
    }
}
