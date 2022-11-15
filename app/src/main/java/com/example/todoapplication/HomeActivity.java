package com.example.todoapplication;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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
    private ExtendedFloatingActionButton createTask;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;
    private DrawerLayout mDrawerLayout;

    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createTask = findViewById(R.id.createTask);

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
        adapter= new TaskAdapter(this, list);
        taskView.setAdapter(adapter);
        taskView.setLayoutManager(new LinearLayoutManager(this));

        LinearProgressIndicator progressIndicator = findViewById(R.id.progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    TaskModel taskModel = ds.getValue(TaskModel.class);
                    list.add(taskModel);
                }
                adapter.notifyDataSetChanged();
                progressIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        taskView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    createTask.shrink();
                else if (dy < 0)
                    createTask.extend();
            }
        });

        MaterialToolbar toolbar = findViewById(R.id.homeToolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.search:
                    Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.sort:
                    Toast.makeText(this, "Sort", Toast.LENGTH_SHORT).show();
                    reference.orderByChild("task").addValueEventListener(new ValueEventListener() {
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
                    break;
                case R.id.logOut:
                    mAuth.signOut();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                    break;
            }
            return false;
        });

        SearchView searchView = (SearchView) toolbar.findViewById(R.id.search);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search title starts with...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(HomeActivity.this, query, Toast.LENGTH_SHORT).show();
                // filter recycler view when query submitted
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                Toast.makeText(HomeActivity.this, query, Toast.LENGTH_SHORT).show();
                reference.orderByChild("task").startAt(query).endAt(query + "\uf8ff").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear();
                            for (DataSnapshot ds: task.getResult().getChildren()) {
                                TaskModel taskModel = ds.getValue(TaskModel.class);
                                list.add(taskModel);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                return false;
            }
        });
    }

    private void addTask(){
        Intent intent = new Intent(HomeActivity.this, InputActivity.class);
        startActivity(intent);
    }
}