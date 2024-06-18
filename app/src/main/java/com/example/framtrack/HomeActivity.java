package com.example.framtrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager=getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemid=item.getItemId();
                if(itemid==R.id.navigation_home){
                    openFragment(new HomeFragment());
                    return true;
                }

                return false;
            }
        });

        openFragment(new HomeFragment());
    }
    private void openFragment(Fragment fragment){

        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void GetHomeFragment(){




        openFragment(new HomeFragment());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_notifications){
            Toast.makeText(this, "notifications", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}