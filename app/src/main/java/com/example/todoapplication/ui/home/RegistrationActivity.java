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

public class RegistrationActivity extends AppCompatActivity {
    private EditText registrationEmail, registrationPassword;
    private Button registrationButton;
    private TextView registrationLogin;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Get the views
        registrationButton = findViewById(R.id.registrationButton);
        registrationEmail = findViewById(R.id.registrationEmail);
        TextInputLayout registrationEmail_wrapper = findViewById(R.id.registrationEmail_wrapper);
        registrationPassword = findViewById(R.id.registrationPassword);
        TextInputLayout registrationPassword_wrapper = findViewById(R.id.registrationPassword_wrapper);
        registrationLogin = findViewById(R.id.loginRegisterButton);

        //Set the progress dialog
        progressDialog = new ProgressDialog(this);

        //Get the firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //Check if user is already logged in, if so, go back and finished the current activity
        registrationLogin.setOnClickListener(v -> {
            finish();
        });

        //Register the user
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the email and password
                String email = registrationEmail.getText().toString();
                String password = registrationPassword.getText().toString();

                //Check if the email and password are empty
                if (email.isEmpty()) {
                    registrationEmail_wrapper.setError("Email required");
                } else if (password.isEmpty()){
                    registrationPassword_wrapper.setError("Password required");
                } else if(password.length() < 6){
                    //Check if the password is less than 6 characters
                    registrationPassword_wrapper.setError("Password must be at least 6 characters");
                } else if (!email.contains("@") && !email.contains(".")){
                    //Check if the email is valid
                    registrationEmail_wrapper.setError("Invalid email");
                } else {
                    //Show the progress dialog
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    //Register the user
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration failed, please check your information", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}