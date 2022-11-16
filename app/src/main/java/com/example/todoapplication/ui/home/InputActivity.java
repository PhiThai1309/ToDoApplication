package com.example.todoapplication.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.example.todoapplication.R;
import com.example.todoapplication.ui.model.TaskModel;
import com.example.todoapplication.ui.model.ToDoApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.util.Date;

public class InputActivity extends AppCompatActivity {
    private TextInputLayout task_wrapper, description_wrapper;
    private TextInputEditText editTask, editDescription;

    public DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //Get database
        ToDoApplication toDoApplication = new ToDoApplication();
        reference = toDoApplication.getmDatabase();

        //Get the toolbar view inside the activity layout
        MaterialToolbar taskTitle = findViewById(R.id.updateToolbar);
        //Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(taskTitle);

        //Get the views
        task_wrapper = (TextInputLayout) findViewById(R.id.newTask_wrapper);
        editTask = findViewById(R.id.newTask);
        description_wrapper = (TextInputLayout) findViewById(R.id.newDescription_wrapper);
        editDescription = (TextInputEditText) findViewById(R.id.newDescription);

        //Get action when clicked on the toolbar item
        taskTitle.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                //Get action when clicked on the save button
                case R.id.save:
                    //Get the task and description
                    String mTask = editTask.getText().toString().trim();
                    String mDescription = editDescription.getText().toString().trim();
                    String id = reference.push().getKey();
                    //Get the date
                    String date = DateFormat.getDateTimeInstance().format(new Date());

                    //If the task is empty
                    if (mTask.isEmpty()){
                        task_wrapper.setError("Task Required");
                    }else if (mDescription.isEmpty()) {
                        description_wrapper.setError("Description Required");
                    } else {
                        //Create a new object, add the new object with the obl key will override the old data
                        TaskModel taskModel = new TaskModel(mTask, mDescription, id, date);
                        reference.child(id).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //If the task is successful
                                if (task.isSuccessful()){
                                    Toast.makeText(InputActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    //If the task is not successful
                                    String error = task.getException().toString();
                                    Toast.makeText(InputActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                return true;
            }
            return false;
        });
    }

    //Add the ability to navigate back to the parent activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //override on back pressed with a dialog if the user navigate back without saving
    @Override
    public void onBackPressed() {
        //Create a dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Exit confirmation");
        builder.setMessage("You have un-save changes. Are you sure you want to exit?");
        //Set the positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
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

    //Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_app_bar, menu);
        return true;
    }
}