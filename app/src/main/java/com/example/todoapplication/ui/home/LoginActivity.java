package com.example.todoapplication.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView loginRegister;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Check if user is already logged in
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        //Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Get the views
        loginEmail = findViewById(R.id.loginEmail);
        TextInputLayout loginEmail_wrapper = findViewById(R.id.loginEmail_wrapper);
        loginPassword = findViewById(R.id.loginPassword);
        TextInputLayout loginPassword_wrapper = findViewById(R.id.loginPassword_wrapper);
        loginButton = findViewById(R.id.loginButton);
        loginRegister = findViewById(R.id.registerLoginButton);

        //Initialize progress dialog
        progressDialog = new ProgressDialog(this);

        //Set OnClickListeners for the register button
        loginRegister.setOnClickListener(v -> {
            //Start the register activity
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        //Set OnClickListeners for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the email and password
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                //Check if the email and password are empty
                if (email.isEmpty()) {
                    loginEmail_wrapper.setError("Email required");
                    Toast.makeText(LoginActivity.this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()){
                    loginPassword_wrapper.setError("Password required");
                    Toast.makeText(LoginActivity.this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
                } else {
                    //Show progress dialog
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    //Authenticate the user
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Check if the task is successful
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}