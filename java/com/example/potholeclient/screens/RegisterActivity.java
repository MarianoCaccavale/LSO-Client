package com.example.potholeclient.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.potholeclient.R;
import com.example.potholeclient.models.PotholesModel;
import com.example.potholeclient.utils.Costants;
import com.example.potholeclient.utils.Network;

import java.util.LinkedList;

public class RegisterActivity extends AppCompatActivity {

    SensorManager sManager;
    Sensor linearAccelerationSensor;
    float[] calibrationValues = new float[3];
    Location lastPotholeLocation;

    final String TAG = "ACTIVITY_REGISTER";

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {

            if (lastPotholeLocation == null) {
                lastPotholeLocation = location;
            } else {

                Log.v(TAG, "Latitude:" + location.getLatitude());
                Log.v(TAG, "Longitude:" + location.getLongitude());

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        try {

                            Network.sendData(Costants.nickname, location.getLatitude(), location.getLongitude());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.setPriority(10);

                thread.start();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }
    };
    SensorEventListener linearAccelerationCalibrationListner = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            calibrationValues[0] = event.values[0];
            calibrationValues[1] = event.values[1];
            calibrationValues[2] = event.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    SensorEventListener linearAccelerationContinuesListner = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (Math.abs((event.values[2] - calibrationValues[2])) > Costants.tolleranceThreshold) {
                Toast.makeText(getApplicationContext(), "BUCA RILEVATA", Toast.LENGTH_SHORT).show();
                getLocation();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    void unregisterLinearAccelerationSensor() {
        sManager.unregisterListener(linearAccelerationCalibrationListner, linearAccelerationSensor);
    }

    private void startAccellerometro() {

        if (linearAccelerationSensor == null) {
            Log.w("SENSOR", "il sensore è null");
            return;
        }
        sManager.registerListener(linearAccelerationCalibrationListner, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    void getLocation() {

        LocationManager lManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // getting GPS status
        boolean isGPSEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSEnabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int LOCATION_REQUEST_CODE = 1;
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, LOCATION_REQUEST_CODE);
                }
                return;
            }

            lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
            lManager.removeUpdates(locationListenerGPS);

        } else {
            Toast.makeText(this, "GPS disattivato, attivalo", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView explanationTextView = findViewById(R.id.explanationTextView);
        explanationTextView.setText(R.string.calibration_explanation);

        sManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        linearAccelerationSensor = sManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        findViewById(R.id.getCoordinatesBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccellerometro();
                findViewById(R.id.stopCalibrationBTN).setVisibility(View.VISIBLE);
                findViewById(R.id.startTracking).setVisibility(View.INVISIBLE);

                findViewById(R.id.stopCalibrationBTN).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unregisterLinearAccelerationSensor();
                        System.out.println(calibrationValues[0] + " - " + calibrationValues[1] + " - " + calibrationValues[2]);
                        findViewById(R.id.stopCalibrationBTN).setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Calibrazione avvenuta con successo!", Toast.LENGTH_LONG).show();
                        findViewById(R.id.startTracking).setVisibility(View.VISIBLE);
                        TextView explanationTextView = findViewById(R.id.explanationTextView);
                        explanationTextView.setText(R.string.registration_explanation);
                    }
                });
            }
        });

        findViewById(R.id.startTracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sManager.registerListener(linearAccelerationContinuesListner, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);

                findViewById(R.id.startTracking).setVisibility(View.INVISIBLE);

                findViewById(R.id.stopTracking).setVisibility(View.VISIBLE);
                findViewById(R.id.stopTracking).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sManager.unregisterListener(linearAccelerationContinuesListner, linearAccelerationSensor);
                        findViewById(R.id.startTracking).setVisibility(View.VISIBLE);
                        findViewById(R.id.stopTracking).setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}