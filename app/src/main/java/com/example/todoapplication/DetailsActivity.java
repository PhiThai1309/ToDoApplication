package com.example.todoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {
    private DatabaseReference reference;

    private String key;
    private String task;
    private String description;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Get the keys of the task from the previous activity
        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        //Get the views
        MaterialToolbar taskTitle = findViewById(R.id.homeToolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsTitle);
        TextView taskDesc = findViewById(R.id.taskDescription);
        TextView editDate = findViewById(R.id.lastEdit);
        FloatingActionButton updateButton =  findViewById(R.id.updateButton);
        BottomAppBar bottomAppBar = findViewById(R.id.updateBottomAppBar);

        //Set toolbar as the action bar
        setSupportActionBar(taskTitle);

        //Get database
        ToDoApplication toDoApplication = new ToDoApplication();
        reference = toDoApplication.getmDatabase();

        //Get the task details from the database updated with realtime listener
        reference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    task = Objects.requireNonNull(snapshot.child("task").getValue()).toString();
                    description = Objects.requireNonNull(snapshot.child("description").getValue()).toString();
                    date = Objects.requireNonNull(snapshot.child("date").getValue()).toString();
                }
                //Set the title of the task, since toolbar cannot change the value of the title programmatically, we will overwrite the title from the layout
                collapsingToolbarLayout.setTitle(task);
                taskDesc.setText(description);
                editDate.setText("Last edit: " + date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Set the onclick listener for the update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, UpdateActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

        //Set the onclick listener for the bottom app bar
        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                //Delete the task
                case R.id.delButton:
                    reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //If the task is deleted successfully, go back to the home activity
                            if (task.isSuccessful()) {
                                Toast.makeText(DetailsActivity.this, "Task Deleted Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                //If the task is not deleted successfully, show the error message
                                Toast.makeText(DetailsActivity.this, "Task Delete Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}