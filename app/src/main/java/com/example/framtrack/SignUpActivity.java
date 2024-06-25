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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.spec.ECField;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private  static final String TAG="SignUpActivity";
    //private DatabaseReference databaseReference;



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
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String name = nameEditText.getText().toString().trim();
                 String email = emailEditText.getText().toString().trim();
                 String password = passwordEditText.getText().toString().trim();
                 String phone = phoneEditText.getText().toString().trim();

                //Validate Mobile Number using Matcher and Pattern (Regular Expression)
                String mobileRegex = "[6-9][0-9]{9}"; //First no. can be {6,8,9} and rest 9 nos can be any no.
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile (mobileRegex);
                mobileMatcher = mobilePattern. matcher (phone);

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SignUpActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    nameEditText.setError("Name is required");
                    nameEditText.requestFocus();
                    
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this,"Please Enter Email",Toast.LENGTH_SHORT).show();
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignUpActivity.this,"Please Re-Enter Email",Toast.LENGTH_SHORT).show();
                    emailEditText.setError("Valid email is required");
                    emailEditText.requestFocus();
                    
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this,"Please Enter Password",Toast.LENGTH_SHORT).show();
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                   
                }else if (password.length()<6) {
                    Toast.makeText(SignUpActivity.this,"Password should be atleast 6 words",Toast.LENGTH_SHORT).show();
                    passwordEditText.setError("Password too week");
                    passwordEditText.requestFocus();

                } else if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(SignUpActivity.this,"Please Enter Phone",Toast.LENGTH_SHORT).show();
                    phoneEditText.setError("Phone number is required");
                    phoneEditText.requestFocus();
                    
                } else if (phone.length()!=10) {
                    Toast.makeText(SignUpActivity.this,"Please Re-Enter Email",Toast.LENGTH_SHORT).show();
                    phoneEditText.setError("Mobile Number should be 10 digits");
                    phoneEditText.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(SignUpActivity.this,"Please Re-Enter Email",Toast.LENGTH_SHORT).show();
                    phoneEditText.setError("Mobile Number is not valid");
                    phoneEditText.requestFocus();
                } else {
                    registerUser(name,email,password,phone);
                }
            }
        });
    }

    public void registerUser(String name,String email,String password,String phone){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        //Enter User Data into firebase realtime
                        ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(name,phone);

                        //Extract user reference from Database for Registered user
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    //send verification email
                                    firebaseUser.sendEmailVerification();

                                    Toast.makeText(SignUpActivity.this, "Registration successful. Please verify your email", Toast.LENGTH_SHORT).show();

                                    //open home page after sucessfull registration
                                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                    //To Prevent User from returning back to Register Activity on pressing back button after registration
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();//to close Register Activity
                                }else{
                                    Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    } else {
                        //Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            passwordEditText.setError("Your password is too weak. Kindly use a mix of alphabets,numbers and special characters");
                            passwordEditText.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            emailEditText.setError("Your email is invalid or already in use. Kindly re-enter.");
                            emailEditText.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            emailEditText.setError("User is already registered with this email. Kindly use another email.");
                            emailEditText.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

        });


    }
}