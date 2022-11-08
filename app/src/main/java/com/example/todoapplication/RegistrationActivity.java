package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity {
    private EditText registrationEmail, registrationPassword;
    private Button registrationButton;
    private TextView registrationLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationButton = findViewById(R.id.registrationButton);
        registrationEmail = findViewById(R.id.registrationEmail);
        registrationPassword = findViewById(R.id.registrationPassword);
        registrationLogin = findViewById(R.id.loginRegisterButton);

        registrationLogin.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);

        });
    }
}