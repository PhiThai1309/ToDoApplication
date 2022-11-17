package com.example.todoapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapplication.ui.home.DetailsActivity;
import com.example.todoapplication.ui.home.HomeActivity;
import com.example.todoapplication.ui.model.TaskModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<TaskModel> mTasks;

    private String key = "";

    // Constructor
    public TaskAdapter(HomeActivity context, List<TaskModel> tasks) {
        mInflater = LayoutInflater.from(context);
        mTasks = tasks;
    }

    // Create the view holder
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view
        View itemView = mInflater.inflate(R.layout.retrieve_layout, parent, false);
        return new TaskViewHolder(itemView);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        //if task is not null
        if (mTasks != null) {
            // Get the task at the position
            TaskModel current = mTasks.get(position);
            // Set the text of the view holder
            holder.task.setText(current.getTask());
            // Set the key of the view holder
            holder.description.setText(current.getDescription());
        } else {
            // Covers the case of data not being ready yet.
            holder.task.setText("Error");
            holder.description.setText("Error");
        }
        // Set the click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = mTasks.get(position).getId();
                updateTask();
            }
        });
    }

    // Return the size of the data set
    @Override
    public int getItemCount() {
        if (mTasks != null)
            return mTasks.size();
        else return 0;
    }

    //TaskViewHolder class to hold the views
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

    // Create an intent to update the task
    public void updateTask() {
        Intent intent = new Intent(mInflater.getContext(), DetailsActivity.class);
        intent.putExtra("key", key);
        mInflater.getContext().startActivity(intent);
        notifyDataSetChanged();
    }
}
