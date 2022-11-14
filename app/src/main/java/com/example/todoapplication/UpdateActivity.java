package com.example.todoapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class UpdateActivity extends AppCompatActivity {
    private EditText uTask;
    private TextInputLayout uDescription_wrapper;
    private TextInputEditText uDescription;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private String key;
    private String task;
    private String description;

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

        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        reference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    task = snapshot.child("task").getValue().toString();
                    description = snapshot.child("description").getValue().toString();

                }
                uTask.setText(task);
                uDescription.setText(description);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        uTask = (EditText) findViewById(R.id.newTask);
        uTask.setText(task);
        uDescription_wrapper = (TextInputLayout) findViewById(R.id.newDescription_wrapper);
        uDescription = (TextInputEditText) findViewById(R.id.newDescription);
        uDescription.setText(description);

        Button save = (Button) findViewById(R.id.create);
        save.setText("Update");
        Button cancel = (Button) findViewById(R.id.cancel);

        cancel.setOnClickListener(v -> {
            finish();
        });

        save.setOnClickListener(v -> {
            String mTask = uTask.getText().toString().trim();
            String mDescription = uDescription.getText().toString().trim();
            String date = DateFormat.getDateTimeInstance().format(new Date());

            if (mTask.isEmpty()){
                uTask.setError("Task Required");
                return;
            }

            if (mDescription.isEmpty()) {
                uDescription_wrapper.setError("Description Required");
                return;
            } else {
                loader.setMessage("Adding Task");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                TaskModel taskModel = new TaskModel(mTask, mDescription, key, date);
                reference.child(key).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UpdateActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                            finish();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(UpdateActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        }
                    }
                });
                loader.dismiss();
            }
        });
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
}
