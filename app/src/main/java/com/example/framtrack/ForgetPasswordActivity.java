package com.example.framtrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText etResetEmail;
    private Button buttonPwdReset;

    private FirebaseAuth authProfile;
    private  static final String TAG="ForgetPasswordActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        //getSupportActionBar().setTitle("Forget Password");
        etResetEmail=findViewById(R.id.etRestEmail);
        buttonPwdReset=findViewById(R.id.btnResetPwd);

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etResetEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgetPasswordActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    etResetEmail.setError("Email is required");
                    etResetEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter Valid Email", Toast.LENGTH_SHORT).show();
                    etResetEmail.setError("Valid email is required");
                    etResetEmail.requestFocus();
                }else {
                    passwordReset(email);
                }
            }
        });
    }

    private void passwordReset(String email) {
        authProfile=FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgetPasswordActivity.this,"Please check your email for password reset link",Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(ForgetPasswordActivity.this,Login.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        etResetEmail.setError("user doesn't exists or no longer valid.please register again");
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(ForgetPasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                    }

                }
            }
        });
    }
}