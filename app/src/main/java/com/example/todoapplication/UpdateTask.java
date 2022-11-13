package com.example.todoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class UpdateTask extends AppCompatActivity {
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private String key;
    private String task;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth =  FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("TaskList").child(onlineUserID);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Intent intent = getIntent();

        key = intent.getStringExtra("key");
        task = intent.getStringExtra("task");
        description = intent.getStringExtra("description");

        EditText editTaskTitle = findViewById(R.id.editTaskTitle);
        EditText editTaskDescription = findViewById(R.id.editTaskDescription);

        editTaskTitle.setText(task);
        editTaskDescription.setText(description);

        Button updateButton =  findViewById(R.id.updateButton);
        Button delButton = findViewById(R.id.delButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = editTaskTitle.getText().toString();
                description = editTaskDescription.getText().toString();
                String date = DateFormat.getDateInstance().format(new Date());

                TaskModel taskModel = new TaskModel(task, description, key, date);

                reference.child(key).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateTask.this, "Task Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateTask.this, "Task Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateTask.this, "Task Deleted Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UpdateTask.this, "Task Delete Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}