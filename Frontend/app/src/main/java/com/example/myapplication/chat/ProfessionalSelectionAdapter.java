package com.example.myapplication.chat;

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

public class ProfessionalSelectionAdapter extends RecyclerView.Adapter<ProfessionalSelectionAdapter.ProfessionalViewHolder> {

    private List<Professional> professionals;

    public ProfessionalSelectionAdapter(List<Professional> professionals) {
        this.professionals = professionals;
    }

    @NonNull
    @Override
    public ProfessionalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_professional_selection, parent, false);
        return new ProfessionalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessionalViewHolder holder, int position) {
        Professional professional = professionals.get(position);
        holder.nameTv.setText(professional.getName());
        holder.specializationTv.setText(professional.getSpecialization());
        holder.checkBox.setChecked(professional.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            professional.setSelected(isChecked);
        });

        holder.itemView.setOnClickListener(v -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return professionals.size();
    }

    public List<Integer> getSelectedProfessionalIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (Professional prof : professionals) {
            if (prof.isSelected()) {
                selectedIds.add(prof.getProfessionalId());
            }
        }
        return selectedIds;
    }

    static class ProfessionalViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, specializationTv;
        CheckBox checkBox;

        public ProfessionalViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.professionalNameTv);
            specializationTv = itemView.findViewById(R.id.specializationTv);
            checkBox = itemView.findViewById(R.id.professionalCheckBox);
        }
    }
}