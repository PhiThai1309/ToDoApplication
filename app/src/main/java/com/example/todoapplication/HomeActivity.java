package com.example.todoapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView taskView;
    private FloatingActionButton createTask;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createTask = (FloatingActionButton) findViewById(R.id.createTask);

        createTask.setOnClickListener(v -> {
            addTask();
        });

        loader = new ProgressDialog(this);

        taskView = (RecyclerView) findViewById(R.id.taskList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        taskView.setHasFixedSize(true);
        taskView.setLayoutManager(linearLayoutManager);

        mAuth =  FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("TaskList").child(onlineUserID);

        List<TaskModel> list = new ArrayList<>();
        final TaskAdapter adapter = new TaskAdapter(this, list);
        taskView.setAdapter(adapter);
        taskView.setLayoutManager(new LinearLayoutManager(this));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    TaskModel taskModel = ds.getValue(TaskModel.class);
                    list.add(taskModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton logout = (FloatingActionButton) findViewById(R.id.signOut);
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addTask(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.input_file, null));

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        final EditText task = (EditText) dialog.findViewById(R.id.newTask);
        final TextInputLayout description_wrapper = (TextInputLayout) dialog.findViewById(R.id.newDescription_wrapper);
        final TextInputEditText description = (TextInputEditText) dialog.findViewById(R.id.newDescription);
        Button save = (Button) dialog.findViewById(R.id.create);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        save.setOnClickListener(v -> {
            String mTask = task.getText().toString().trim();
            String mDescription = description.getText().toString().trim();
            String id = reference.push().getKey();
            String date = DateFormat.getDateTimeInstance().format(new Date());

            if (mTask.isEmpty()){
                task.setError("Task Required");
                return;
            }

            if (mDescription.isEmpty()) {
                description_wrapper.setError("Description Required");
                return;
            } else {
                loader.setMessage("Adding Task");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                TaskModel taskModel = new TaskModel(mTask, mDescription, id, date);
                reference.child(id).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                            dialog.dismiss();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                            dialog.dismiss();
                        }
                    }
                });

                loader.dismiss();
                dialog.dismiss();
            }
        });
    }
}