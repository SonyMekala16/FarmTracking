package com.example.framtrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private TextView  textViewFullName, textViewEmail,textViewPhone,textViewSignOut;

    private String fullname, email, mobile;

    private FirebaseAuth authProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        textViewFullName = view.findViewById(R.id.tv_showname);
        textViewEmail = view.findViewById(R.id.tv_showemail);
        textViewPhone = view.findViewById(R.id.tv_showphone);
        textViewSignOut=view.findViewById(R.id.tv_Signout);

        textViewSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "Something went wrong! users details are not available at the moment", Toast.LENGTH_SHORT).show();

        } else {
            checkEmailVerified(firebaseUser);

            showUserProfile(firebaseUser);
        }

        return view;
    }

    private void signout(){
        authProfile=FirebaseAuth.getInstance();
        if(authProfile.getCurrentUser()!=null){
            authProfile.signOut();
            Toast.makeText(getActivity(),"Logged Out",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(), Login.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


            //startActivity(new Intent(Login.this,HomeActivity.class));
            //finish();
        }else {
            Toast.makeText(getActivity(),"You are still Logged now",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //set alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Email not Verified");
        builder.setMessage("Please verify your email now.You can't login with the email verification from next time");

        //open email app if user clicks/taps on continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//opens email in new window
                startActivity(intent);
            }
        });

        //create alert dialog
        AlertDialog alertDialog = builder.create();

        //show alert dialog
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //extracting user reference from db from registered users
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullname = readUserDetails.name;
                    email = firebaseUser.getEmail();
                    mobile = readUserDetails.phone;


                    textViewFullName.setText(fullname);
                    textViewEmail.setText(email);
                    textViewPhone.setText(mobile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();

            }
        });
    }

}