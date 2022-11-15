package com.example.todoapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<TaskModel> mTasks;

    private String key = "";

    public TaskAdapter(HomeActivity context, List<TaskModel> tasks) {
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
            TaskModel current = mTasks.get(position);
            holder.task.setText(current.getTask());
            holder.description.setText(current.getDescription());
        } else {
            // Covers the case of data not being ready yet.
            holder.task.setText("Error");
            holder.description.setText("Error");
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = mTasks.get(position).getId();
                updateTask();
            }
        });
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
        MaterialCardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.task);
            description = itemView.findViewById(R.id.description);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public void updateTask() {
        Intent intent = new Intent(mInflater.getContext(), DetailsActivity.class);
        intent.putExtra("key", key);
        mInflater.getContext().startActivity(intent);
        notifyDataSetChanged();
    }
}
