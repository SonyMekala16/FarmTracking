/*package com.example.framtrack;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class HomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //return inflater.inflate(R.layout.fragment_home, container, false);
        CardView livestockProfiles = view.findViewById(R.id.cvlivestockprofiles);
        CardView realTimeTracking = view.findViewById(R.id.cvlocationtracking);
        CardView nodeManagement = view.findViewById(R.id.cvnodemanagement);
        CardView historyAnalytics = view.findViewById(R.id.cvhistory);
        CardView dataSyncOffline = view.findViewById(R.id.cvdatasync);
        CardView supportHelp = view.findViewById(R.id.cvsupport);

        livestockProfiles.setOnClickListener(v -> {
            // Handle Livestock Profiles click
            Toast.makeText(getContext(), "Livestock Profiles Clicked", Toast.LENGTH_SHORT).show();
        });

        realTimeTracking.setOnClickListener(v -> {
            // Handle Real-Time Location Tracking click
            Toast.makeText(getContext(), "Real-Time Location Tracking Clicked", Toast.LENGTH_SHORT).show();
        });

        nodeManagement.setOnClickListener(v -> {
            // Handle Node Management click
            Toast.makeText(getContext(), "Node Management Clicked", Toast.LENGTH_SHORT).show();
        });

        historyAnalytics.setOnClickListener(v -> {
            // Handle History and Analytics click
            Toast.makeText(getContext(), "History and Analytics Clicked", Toast.LENGTH_SHORT).show();
        });

        dataSyncOffline.setOnClickListener(v -> {
            // Handle Data Sync and Offline Mode click
            Toast.makeText(getContext(), "Data Sync and Offline Mode Clicked", Toast.LENGTH_SHORT).show();
        });

        supportHelp.setOnClickListener(v -> {
            // Handle Support and Help click
            Toast.makeText(getContext(), "Support and Help Clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}*/


package com.example.framtrack;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CardView livestockProfiles = view.findViewById(R.id.cvlivestockprofiles);
        CardView realTimeTracking = view.findViewById(R.id.cvlocationtracking);
        CardView nodeManagement = view.findViewById(R.id.cvnodemanagement);
        CardView historyAnalytics = view.findViewById(R.id.cvhistory);
        CardView dataSyncOffline = view.findViewById(R.id.cvdatasync);
        CardView supportHelp = view.findViewById(R.id.cvsupport);

        livestockProfiles.setOnClickListener(v -> {
            // Handle Livestock Profiles click
            Toast.makeText(getContext(), "Livestock Profiles Clicked", Toast.LENGTH_SHORT).show();
        });

        realTimeTracking.setOnClickListener(v -> {
            // Handle Real-Time Location Tracking click
            openMapsFragment();
        });

        nodeManagement.setOnClickListener(v -> {
            // Handle Node Management click
            Toast.makeText(getContext(), "Node Management Clicked", Toast.LENGTH_SHORT).show();
        });

        historyAnalytics.setOnClickListener(v -> {
            // Handle History and Analytics click
            Toast.makeText(getContext(), "History and Analytics Clicked", Toast.LENGTH_SHORT).show();
        });

        dataSyncOffline.setOnClickListener(v -> {
            // Handle Data Sync and Offline Mode click
            Toast.makeText(getContext(), "Data Sync and Offline Mode Clicked", Toast.LENGTH_SHORT).show();
        });

        supportHelp.setOnClickListener(v -> {
            // Handle Support and Help click
            Toast.makeText(getContext(), "Support and Help Clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void openMapsFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, new MapsFragment());
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack to allow users to navigate back
        fragmentTransaction.commit();
    }
}
