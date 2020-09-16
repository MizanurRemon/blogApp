package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
public class splashscreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splashscreen.this, MainActivity.class));
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}