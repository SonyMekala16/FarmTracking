package com.example.framtrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

public class LauncherScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000; // Duration in milliseconds

    //private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_screen);

       // progressBar=findViewById(R.id.progressBar);


       // showProgressBar();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your app's main activity
               // hideProgressBar();
                Intent intent = new Intent(LauncherScreen.this, Login.class);
                startActivity(intent);
                // Close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
   /* private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }*/
}