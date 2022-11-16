package com.example.todoapplication.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapplication.R;
import com.example.todoapplication.TaskAdapter;
import com.example.todoapplication.ui.model.TaskModel;
import com.example.todoapplication.ui.model.ToDoApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView taskView;
    private ExtendedFloatingActionButton createTask;
    private List<TaskModel> list;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    private TaskAdapter adapter;
    private LinearProgressIndicator progressIndicator;

    private int checkedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createTask = findViewById(R.id.createTask);

        createTask.setOnClickListener(v -> {
            addTask();
        });

        taskView = (RecyclerView) findViewById(R.id.taskList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        taskView.setHasFixedSize(true);
        taskView.setLayoutManager(linearLayoutManager);

        //Get database
        ToDoApplication toDoApplication = new ToDoApplication();
        reference = toDoApplication.getmDatabase();
        mAuth = toDoApplication.getmAuth();

        list = new ArrayList<>();
        adapter = new TaskAdapter(this, list);
        taskView.setAdapter(adapter);
        taskView.setLayoutManager(new LinearLayoutManager(this));

        progressIndicator = findViewById(R.id.progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        switchFilter(checkedItem);

        taskView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 10)
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
                    showAlertDialog();
                    break;
                case R.id.logOut:
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                    builder.setTitle("You are logging out");
                    builder.setMessage("Do you want to continue?");
                    //Set the positive button
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
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
                queryTask(query, list);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                queryTask(query, list);
                return false;
            }
        });
    }

    private void addTask(){
        Intent intent = new Intent(HomeActivity.this, InputActivity.class);
        startActivity(intent);
    }

    private void queryTask(String query, List<TaskModel> list) {
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
    }

    private void showAlertDialog() {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(HomeActivity.this);
        alertDialog.setTitle("Filter options");
        String[] items = {"Default filter", "Sort by title: Ascending", "Sort by date: Ascending", "Sort by title: Descending", "Sort by date: Descending"};

        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchFilter(which);
            }
        });

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    private void fetchData(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    TaskModel taskModel = ds.getValue(TaskModel.class);
                    list.add(taskModel);
                }
                progressIndicator.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataWithFilter(String option) {
        reference.orderByChild(option).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    TaskModel taskModel = ds.getValue(TaskModel.class);
                    list.add(taskModel);
                }
                progressIndicator.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataWithFilterReverse(String option) {
        reference.orderByChild(option).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    TaskModel taskModel = ds.getValue(TaskModel.class);
                    list.add(taskModel);
                }
                Collections.reverse(list);
                progressIndicator.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterBy(String option) {
        if(checkedItem > 0 && checkedItem < 3) {
            fetchDataWithFilter(option);
        } else if (checkedItem >= 3 && checkedItem < 5) {
            fetchDataWithFilterReverse(option);
        } else {
            fetchData();
        }
    }

    private void switchFilter(int option) {
        switch (option) {
            case 0:
                checkedItem =  0;
                filterBy("");
                break;
            case 1:
                checkedItem =  1;
                filterBy("task");
                break;
            case 2:
                checkedItem =  2;
                filterBy("date");
                break;
            case 3:
                checkedItem = 3;
                filterBy("task");
                break;
            case 4:
                checkedItem = 4;
                filterBy("date");
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        switchFilter(checkedItem);
    }
}