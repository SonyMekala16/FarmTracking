package com.example.framtrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ImageButton btnGoogle;
    private ImageButton btnFacebook;
    private ImageButton btnTwitter;
    private TextView tvSignUp;
    private TextView tvforgetpassword;
    private FirebaseAuth authProfile;
    private  static final String TAG="LogInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnTwitter = findViewById(R.id.btnTwitter);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvforgetpassword=findViewById(R.id.forget_pass);
        tvforgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this,"You can reset the password now",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Login.this,ForgetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        authProfile=FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this,"Please Enter Email",Toast.LENGTH_SHORT).show();
                    etEmail.setError("Email is required");
                    etEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(Login.this,"Please Re-Enter Email",Toast.LENGTH_SHORT).show();
                    etEmail.setError("Valid email is required");
                    etEmail.requestFocus();

                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this,"Please Enter Password",Toast.LENGTH_SHORT).show();
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();

                }else {
                    loginUser(email,password);
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Login Activity
                Intent intent = new Intent(Login.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(Login.this,"You are logged in now",Toast.LENGTH_SHORT).show();

                    //get instance of the user
                    FirebaseUser firebaseUser=authProfile.getCurrentUser();

                    //check if email is verified or not
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(Login.this,"You are logged in now",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Login.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }

                }
                else {
                    //Toast.makeText(Login.this,"login failed",Toast.LENGTH_SHORT).show();
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthInvalidUserException e) {
                        etEmail.setError("User does not exists or is no longer valid. Please register again.");
                        etEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        etEmail.setError("Invalid credentials. Kindly, check and re-enter.");
                        etEmail.requestFocus();
                    }catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast. makeText ( Login. this, e.getMessage(), Toast.LENGTH_SHORT). show();
                            }
                }
            }
        });

    }

    private void showAlertDialog() {
        //set alert builder
        AlertDialog.Builder builder=new AlertDialog.Builder(Login.this);
        builder.setTitle("Email not Verified");
        builder.setMessage("Please verify your email now.You can't login with the email verification");

        //open email app if user clicks/taps on continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//opens email in new window
                startActivity(intent);
            }
        });

        //create alertdiLOG
        AlertDialog alertDialog=builder.create();

        //show alert dialog
        alertDialog.show();
    }


    //check if user is already loggedin or not
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!=null){
            Toast.makeText(Login.this,"You have already logged in",Toast.LENGTH_SHORT).show();

            //start homepage

            startActivity(new Intent(Login.this,HomeActivity.class));
            finish();
        }else {
            Toast.makeText(Login.this,"You can Login now",Toast.LENGTH_SHORT).show();
        }
    }
}