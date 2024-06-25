package com.example.framtrack;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class UserProfileFragment extends Fragment {

    private Button signoutbtn;
    private FirebaseAuth authProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_profile, container, false);
        signoutbtn=view.findViewById(R.id.btnsignout);
        authProfile=FirebaseAuth.getInstance();
        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        return view;
    }
}