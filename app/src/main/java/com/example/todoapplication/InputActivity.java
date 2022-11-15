package com.example.todoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class InputActivity extends AppCompatActivity {
    private EditText task;
    private TextInputLayout description_wrapper;
    private TextInputEditText description;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        loader = new ProgressDialog(this);

        mAuth =  FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("TaskList").child(onlineUserID);

        MaterialToolbar taskTitle = findViewById(R.id.homeToolbar);
        setSupportActionBar(taskTitle);

        task = (EditText) findViewById(R.id.newTask);
        description_wrapper = (TextInputLayout) findViewById(R.id.newDescription_wrapper);
        description = (TextInputEditText) findViewById(R.id.newDescription);

        taskTitle.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.save:
                    String mTask = task.getText().toString().trim();
                    String mDescription = description.getText().toString().trim();
                    String id = reference.push().getKey();
                    String date = DateFormat.getDateTimeInstance().format(new Date());

                    if (mTask.isEmpty()){
                        task.setError("Task Required");
                    }
                    if (mDescription.isEmpty()) {
                        description_wrapper.setError("Description Required");
                    } else {
                        loader.setMessage("Adding Task");
                        loader.setCanceledOnTouchOutside(false);
                        loader.show();

                        TaskModel taskModel = new TaskModel(mTask, mDescription, id, date);
                        reference.child(id).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(InputActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                                    loader.dismiss();
                                    finish();
                                } else {
                                    String error = task.getException().toString();
                                    Toast.makeText(InputActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                                    loader.dismiss();
                                }
                            }
                        });
                        loader.dismiss();
                    }
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Back");
        builder.setMessage("R.string.on_back_button_message");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_app_bar, menu);
        return true;
    }
}