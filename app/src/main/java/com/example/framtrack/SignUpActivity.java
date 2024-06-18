package com.example.framtrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneEditText;
    private Button signUpButton;
    private ImageButton googleSignUpButton;
    private ImageButton facebookSignUpButton;
    private ImageButton twitterSignUpButton;
    private TextView signInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText=findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        phoneEditText = findViewById(R.id.phonenumber);
        signUpButton = findViewById(R.id.signupbtn);
        googleSignUpButton = findViewById(R.id.btngoogle);
        facebookSignUpButton = findViewById(R.id.btnfacebook);
        twitterSignUpButton = findViewById(R.id.btntwitter);
        signInTextView = findViewById(R.id.tvsignin);
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Login Activity
                Intent intent = new Intent(SignUpActivity.this, Login.class);
                startActivity(intent);
            }
        });
    }
}