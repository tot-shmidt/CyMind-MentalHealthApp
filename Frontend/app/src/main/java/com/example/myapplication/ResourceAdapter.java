package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {
    private List<Resource> resourceList;
    private Context context;
    private boolean isProfessional;
    private OnResourceClickListener listener;

    public interface OnResourceClickListener {
        void onResourceClick(Resource resource, int position);
    }
    public ResourceAdapter(List<Resource> resourceList, Context context, boolean isProfessional, OnResourceClickListener listener) {
        this.resourceList = resourceList;
        this.context = context;
        this.isProfessional = isProfessional;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, preview, categories;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.resourceTitle);
            author = view.findViewById(R.id.resourceAuthor);
            preview = view.findViewById(R.id.resourcePreview);
            categories = view.findViewById(R.id.resourceCategories);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resource_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Resource resource = resourceList.get(position);

        holder.title.setText(resource.getTitle() != null ? resource.getTitle() : "");
        holder.author.setText(resource.getAuthor() != null ? resource.getAuthor() : "");
        holder.categories.setText(resource.getCategories() != null ? resource.getCategories() : "");
        holder.preview.setText(resource.getDescription() != null ? resource.getDescription() : "");


        holder.itemView.setOnClickListener(v -> {
            if (isProfessional) {
                // Professional behavior: open update/delete dialog
                listener.onResourceClick(resource, position);
            } else {
                // Student behavior: open info dialog
                showResourceDialog(resource);
            }
        });
    }

    private void showResourceDialog(Resource resource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.resource_dialog, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.dialogTitle);
        TextView author = dialogView.findViewById(R.id.dialogAuthor);
        TextView categories = dialogView.findViewById(R.id.dialogCategories);
        TextView description = dialogView.findViewById(R.id.dialogDescription);

        title.setText(resource.getTitle());
        author.setText("By " + resource.getAuthor());
        categories.setText(resource.getCategories());
        description.setText(resource.getDescription());

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return resourceList.size();
    }
}
