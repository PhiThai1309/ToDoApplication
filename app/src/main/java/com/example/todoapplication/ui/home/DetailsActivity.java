package com.example.todoapplication.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapplication.R;
import com.example.todoapplication.ui.model.ToDoApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
            //Delete the task
            if (item.getItemId() == R.id.delButton) {
                deleteConfirmation();
                return true;
            }
            return false;
        });
    }

    //Override the back button to go back to the home activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //override on back pressed with a dialog if the user click delete button
    public void deleteConfirmation() {
        //Create a dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Delete confirmation");
        builder.setMessage("Do you want to delete this task?");
        //Set the positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
            }
        });
        //Set the negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}