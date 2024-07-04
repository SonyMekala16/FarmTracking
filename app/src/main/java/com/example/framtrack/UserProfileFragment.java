package com.example.framtrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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


public class UserProfileFragment extends Fragment {

    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewPhone;
    private ProgressBar progressBar;
    private String fullname, email, mobile;
    private ImageView imageView;
    private Button signoutbtn;
    private FirebaseAuth authProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true);
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        textViewWelcome = view.findViewById(R.id.tv_show_welcome);
        textViewFullName = view.findViewById(R.id.tv_show_name);
        textViewEmail = view.findViewById(R.id.tv_show_email);
        textViewPhone = view.findViewById(R.id.tv_show_phone);
        progressBar = view.findViewById(R.id.progressbar);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "Something went wrong! users details are not available at the moment", Toast.LENGTH_SHORT).show();

        } else {
            checkEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
        /*signoutbtn=view.findViewById(R.id.btnsignout);
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
        });*/

        MenuProvider menuProvider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_refresh) {
                    // Handle refresh action
                    refreshFragment();
                    return true;
                } /*else if (id == R.id.menu_update_profile) {
                    ntent intent=new Intent(UserProfileFragment.this,UpdateProfileActivity.class);
                    startActivity(intent);
                    return true;
                }else if(id==R.id.menu_update_email){
                    Intent intent=new Intent(UserProfileFragment.this,UpdateEmailActivity.class);
                    startActivity(intent);
                    return true;
                }*/ else if (id==R.id.menu_logout) {
                    signout();

                    return true;
                }
                return false;
            }

        };
        requireActivity().addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return view;






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

                    textViewWelcome.setText("Welcome, " + fullname + "!");
                    textViewFullName.setText(fullname);
                    textViewEmail.setText(email);
                    textViewPhone.setText(mobile);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /*
        @Override
        public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.profile_menu,menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            int id=item.getItemId();
            if(id==R.id.menu_refresh){
                //Refresh Activity
                //startActivity(getIntent());
                refreshFragment();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }*/

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

    private void refreshFragment() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Simulate a refresh task (e.g., fetching data)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Refresh the fragment
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(UserProfileFragment.this).attach(UserProfileFragment.this).commit();

                // Hide progress bar after refreshing
                progressBar.setVisibility(View.GONE);
            }
        }, 2000); // 2 seconds delay for simulation
    }
}