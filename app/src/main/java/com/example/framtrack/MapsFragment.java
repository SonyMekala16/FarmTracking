package com.example.framtrack;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";
    private static final String HC_05_MAC_ADDRESS = "98:D3:31:F9:FF:D9"; // Replace with your HC-05 MAC address
    private static final UUID HC_05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;

    // Firebase Database references for nodes
    private DatabaseReference mDatabaseNode1;
    private DatabaseReference mDatabaseNode2;
    private DatabaseReference mDatabaseNode3;
    private DatabaseReference mDatabaseNode4;

    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        // Initialize Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            return view;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(getContext(), "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Initialize Firebase Database references
        mDatabaseNode1 = FirebaseDatabase.getInstance().getReference("locations/node1");
        mDatabaseNode2 = FirebaseDatabase.getInstance().getReference("locations/node2");
        mDatabaseNode3 = FirebaseDatabase.getInstance().getReference("locations/node3");
        mDatabaseNode4 = FirebaseDatabase.getInstance().getReference("locations/node4");

        // Set up the map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap map) {
                    googleMap = map;

                    // Initialize Bluetooth connection and listen for sheep1 data
                    connectToBluetoothDevice();
                    readDataFromBluetooth();

                    // Retrieve data from Firebase for other nodes and update the map
                    addLocationMarker(mDatabaseNode1, "Node 1", BitmapDescriptorFactory.HUE_RED);
                    addLocationMarker(mDatabaseNode2, "Node 2", BitmapDescriptorFactory.HUE_RED);
                    addLocationMarker(mDatabaseNode3, "Node 3", BitmapDescriptorFactory.HUE_RED);
                    addLocationMarker(mDatabaseNode4, "Node 4", BitmapDescriptorFactory.HUE_RED);

                    // Optional: Add click listener for map
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng latLng) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .title(latLng.latitude + " : " + latLng.longitude);

                            googleMap.addMarker(markerOptions);
                        }
                    });
                }
            });
        }

        return view;
    }

    private void connectToBluetoothDevice() {
        try {
            BluetoothDevice hc05 = bluetoothAdapter.getRemoteDevice(HC_05_MAC_ADDRESS);
            bluetoothSocket = hc05.createRfcommSocketToServiceRecord(HC_05_UUID);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            Toast.makeText(getContext(), "Connected to HC-05", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Bluetooth device", e);
            Toast.makeText(getContext(), "Failed to connect to HC-05", Toast.LENGTH_SHORT).show();
        }
    }

    private void readDataFromBluetooth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;
                StringBuilder dataString = new StringBuilder();

                while (true) {
                    try {
                        bytes = inputStream.read(buffer);
                        String readMessage = new String(buffer, 0, bytes);
                        dataString.append(readMessage);

                        // Check if the data contains the expected format and a newline indicating the end of a message
                        if (dataString.toString().contains("\n")) {
                            // Example: "Estimated Sheep1 Location: Lat=52.97016, Lon=-1.159121"
                            String data = dataString.toString().trim();

                            // Extract latitude and longitude
                            try {
                                if(data.contains("Sheep1")){
                                String[] parts = data.split("[,=]");
                                String latitudeStr = parts[1].trim();
                                String longitudeStr = parts[3].trim();

                                // Parse the latitude and longitude
                                double latitude = Double.parseDouble(latitudeStr);
                                double longitude = Double.parseDouble(longitudeStr);
                                LatLng location = new LatLng(latitude, longitude);

                                // Update the map on the UI thread
                                getActivity().runOnUiThread(() -> {
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(location)
                                            .title("Sheep 1")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                    googleMap.addMarker(markerOptions);
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                                });} else if (data.contains("Sheep2")) {
                                    String[] parts = data.split("[,=]");
                                    String latitudeStr = parts[1].trim();
                                    String longitudeStr = parts[3].trim();

                                    // Parse the latitude and longitude
                                    double latitude = Double.parseDouble(latitudeStr);
                                    double longitude = Double.parseDouble(longitudeStr);
                                    LatLng location = new LatLng(latitude, longitude);

                                    // Update the map on the UI thread
                                    getActivity().runOnUiThread(() -> {
                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position(location)
                                                .title("Sheep 1")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                                        googleMap.addMarker(markerOptions);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                                    });

                                }
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                Log.e(TAG, "Error parsing latitude/longitude", e);
                            }

                            dataString.setLength(0); // Clear the buffer after processing
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading from Bluetooth device", e);
                        break;
                    }
                }
            }
        }).start();
    }

    private void addLocationMarker(DatabaseReference databaseReference, String markerTitle, float markerColor) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String longitude = dataSnapshot.child("longitude").getValue(String.class);
                    String latitude = dataSnapshot.child("latitude").getValue(String.class);

                    if (longitude != null && latitude != null) {
                        double lat = Double.parseDouble(latitude);
                        double lng = Double.parseDouble(longitude);
                        LatLng location = new LatLng(lat, lng);

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .title(markerTitle)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor));

                        googleMap.addMarker(markerOptions);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (inputStream != null) inputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth connection", e);
        }
    }
}
