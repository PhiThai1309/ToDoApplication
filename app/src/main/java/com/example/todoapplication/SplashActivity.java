package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.todoapplication.ui.home.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    Animation splashScreen;
    TextView appName;
    LinearLayout splashContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the views
        splashScreen = AnimationUtils.loadAnimation(this, R.anim.splash_screen);
        splashContent = findViewById(R.id.splashContent);

        //Start the animation
        splashContent.startAnimation(splashScreen);

        // Splash Screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}