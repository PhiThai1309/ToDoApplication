package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class UpdateTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        String task = intent.getStringExtra("task");
        String date = intent.getStringExtra("date");
        String description = intent.getStringExtra("description");

        EditText editTaskTitle = findViewById(R.id.editTaskTitle);
        EditText editTaskDescription = findViewById(R.id.editTaskDescription);

        editTaskTitle.setText(task);
        editTaskDescription.setText(description);
    }
}