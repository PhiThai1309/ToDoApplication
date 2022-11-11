package com.example.todoapplication;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<Model> mTasks;

    public TaskAdapter(HomeActivity context, List<Model> tasks) {
        mInflater = LayoutInflater.from(context);
        mTasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.retrieve_layout, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (mTasks != null) {
            Model current = mTasks.get(position);
            holder.task.setText(current.getTask());
            holder.description.setText(current.getDescription());
        } else {
            // Covers the case of data not being ready yet.
            holder.task.setText("Error");
            holder.description.setText("Error");
        }
    }

    @Override
    public int getItemCount() {
        if (mTasks != null)
            return mTasks.size();
        else return 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView task;
        TextView description;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.task);
            description = itemView.findViewById(R.id.description);
        }
    }
}