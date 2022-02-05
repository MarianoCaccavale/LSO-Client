package com.example.potholeclient.screens;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.potholeclient.R;
import com.example.potholeclient.models.PotholesModel;
import com.example.potholeclient.utils.Costants;
import com.example.potholeclient.utils.Network;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class PotholesListScreen extends AppCompatActivity {

    LinkedList<PotholesModel> potholesList;
    Location myLocation;
    FusedLocationProviderClient fusedLocationClient;
    LocationManager lManager;


    Thread getterThread = new Thread(new Runnable() {
        @Override
        public void run(){

            // getting GPS status
            boolean isGPSEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int LOCATION_REQUEST_CODE = 1;
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, LOCATION_REQUEST_CODE);
                    }
                    return;
                }

                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        myLocation = location;

                        if (myLocation != null) {

                            Thread bho = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    potholesList = Network.receiveData(Costants.nickname, myLocation.getLatitude(), myLocation.getLongitude());

                                    if (!potholesList.isEmpty()) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                final ArrayAdapter<PotholesModel> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, potholesList);

                                                ListView potholesListView = findViewById(R.id.potholesListView);
                                                potholesListView.setClickable(false);

                                                potholesListView.setAdapter(adapter);
                                            }
                                        });

                                    }
                                }
                            });

                            bho.setPriority(10);
                            bho.start();
                        }
                    }
                });

                fusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "IMPOSSIBILE OTTENERE LA LISTA DELLE BUCHE", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "GPS disattivato, attivalo", Toast.LENGTH_LONG).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potholes_list_screen);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        lManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        getterThread.setPriority(10);
        getterThread.start();

    }
}