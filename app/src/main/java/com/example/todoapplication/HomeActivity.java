package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {
    private FloatingActionButton createTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createTask = (FloatingActionButton) findViewById(R.id.createTask);

        createTask.setOnClickListener(v -> {
//            Intent intent = new Intent(this, CreateTaskActivity.class);
//            startActivity(intent);
        });
    }
}