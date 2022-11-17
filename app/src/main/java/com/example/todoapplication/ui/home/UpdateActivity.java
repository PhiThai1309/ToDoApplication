package com.example.todoapplication.ui.home;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapplication.R;
import com.example.todoapplication.ui.model.TaskModel;
import com.example.todoapplication.ui.model.ToDoApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {
    private TextInputLayout uTask_wrapper, uDescription_wrapper;
    private TextInputEditText uTask, uDescription;

    private DatabaseReference reference;

    private ProgressDialog progressDialog;

    private String key;
    private String task;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //Initialize progress dialog
        progressDialog = new ProgressDialog(this);

        //Get the views
        MaterialToolbar taskTitle = findViewById(R.id.updateToolbar);
        uTask_wrapper = (TextInputLayout) findViewById(R.id.newTask_wrapper);
        uTask = findViewById(R.id.newTask);
        uDescription_wrapper = (TextInputLayout) findViewById(R.id.newDescription_wrapper);
        uDescription = (TextInputEditText) findViewById(R.id.newDescription);

        //Set the title of the toolbar for the update activity
        taskTitle.setTitle("Update Task");

        //Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(taskTitle);

        //Set text to the view
        uTask.setText(task);
        uDescription.setText(description);

        //Get intent from another activity
        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        //Get database
        ToDoApplication toDoApplication = new ToDoApplication();
        reference = toDoApplication.getmDatabase();

        //Get realtime data
        reference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if the data is not empty
                if (snapshot.exists()) {
                    task = Objects.requireNonNull(snapshot.child("task").getValue()).toString();
                    description = Objects.requireNonNull(snapshot.child("description").getValue()).toString();

                }
                //Set the data to the views
                uTask.setText(task);
                uDescription.setText(description);
            }

            //If the operation is  unsuccessful
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        //Assign a click listener to the button
        taskTitle.setOnMenuItemClickListener(item -> {
            //If the user clicks on the save button
            if (item.getItemId() == R.id.save) {//Get the data from the views
                String mTask = uTask.getText().toString().trim();
                String mDescription = uDescription.getText().toString().trim();
                //Create a new date
                String date = DateFormat.getDateTimeInstance().format(new Date());

                //If the data is not empty
                if (mTask.isEmpty()) {
                    uTask_wrapper.setError("Task Required");
                } else if (mDescription.isEmpty()) {
                    uDescription_wrapper.setError("Description Required");
                } else {
                    //Show progress dialog
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    //Create a new object, add the new object with the obl key will override the old data
                    TaskModel taskModel = new TaskModel(mTask, mDescription, key, date);
                    reference.child(key).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            //If the operation is successful
                            if (task.isSuccessful()) {
                                Toast.makeText(UpdateActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                //If the operation is unsuccessful
                                String error = task.getException().toString();
                                Toast.makeText(UpdateActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return true;
            }
            return false;
        });

    }

    //Navigate to the previous activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //Add alert dialog to the activity if the user navigate back without saving
    @Override
    public void onBackPressed() {
        //Create a new alert dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Exit confirmation");
        builder.setMessage("You have unsaved changes. Are you sure you want to exit?");
        //If the user clicks on the yes button
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        //If the user clicks on the no button
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    //Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_app_bar, menu);
        return true;
    }
}
