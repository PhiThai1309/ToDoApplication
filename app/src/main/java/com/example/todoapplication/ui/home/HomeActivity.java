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

        //Get the floating action button
        createTask = findViewById(R.id.createTask);

        //Set on click listener for floating action button
        createTask.setOnClickListener(v -> {
            addTask();
        });

        //Get the recycler view and
        taskView = (RecyclerView) findViewById(R.id.taskList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        taskView.setHasFixedSize(true);
        taskView.setLayoutManager(linearLayoutManager);

        //Get database
        ToDoApplication toDoApplication = new ToDoApplication();
        reference = toDoApplication.getmDatabase();
        mAuth = toDoApplication.getmAuth();

        //Initialize the list and the adapter
        list = new ArrayList<>();
        adapter = new TaskAdapter(this, list);
        taskView.setAdapter(adapter);

        //Get the progress indicator
        progressIndicator = findViewById(R.id.progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        //Load the data from the database to the recycler view
        switchFilter(checkedItem);

        //Get on scroll listener for the recycler view to shrink the floating action button
        taskView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 10)
                    createTask.shrink();
                else if (dy < 0)
                    createTask.extend();
            }
        });

        //Get the toolbar
        MaterialToolbar toolbar = findViewById(R.id.homeToolbar);
        //Set the on click listener for the toolbar
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                //If the user clicks on the filter button
                case R.id.sort:
                    showAlertDialog();
                    break;
                //If the user clicks on the logout button
                case R.id.logOut:
                    //Initialize the alert dialog to confirm the logout
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

        //Get the search view
        SearchView searchView = (SearchView) toolbar.findViewById(R.id.search);
        //Set the with of the search view to tale full width
        searchView.setMaxWidth(Integer.MAX_VALUE);
        //Set hint for the user
        searchView.setQueryHint("Search title starts with...");
        //Set on query text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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

    //Method to add a task
    private void addTask(){
        Intent intent = new Intent(HomeActivity.this, InputActivity.class);
        startActivity(intent);
    }

    //Method to query the task, since realtime database can only support query by start at, our search function is limited to this.
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

    //Method to switch the filter of filtering function
    private void showAlertDialog() {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(HomeActivity.this);
        alertDialog.setTitle("Filter options");
        String[] items = {"Default filter", "Sort by title: Descending", "Sort by date: Descending", "Sort by title: Ascending", "Sort by date: Ascending"};

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

    //Method to fetch data with original order
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

    //Method to fetch data with the order from the parameter string
    private void fetchDataWithFilter(String option) {
        reference.orderByChild(option).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    TaskModel taskModel = ds.getValue(TaskModel.class);
                    list.add(taskModel);
                }
                //Since realtime database does not provide the option to reverse, we will have to reverse the list by ourself
                if (checkedItem >= 3 && checkedItem < 5) {
                    Collections.reverse(list);
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

    //Method to switch the filter
    private void switchFilter(int option) {
        switch (option) {
            case 0:
                checkedItem =  0;
                fetchData();
                break;
            case 1:
                checkedItem =  1;
                fetchDataWithFilter("task");
                break;
            case 2:
                checkedItem =  2;
                fetchDataWithFilter("date");
                break;
            case 3:
                checkedItem = 3;
                fetchDataWithFilter("task");
                break;
            case 4:
                checkedItem = 4;
                fetchDataWithFilter("date");
                break;
        }
    }

    //If user navigate s back to this activity, we will sort the data again to ensure with user previous choice of sorting
    @Override
    protected void onRestart() {
        super.onRestart();
        switchFilter(checkedItem);
    }
}