package com.example.eakgun14.journeytracker.RouteService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class RouteService extends Service {
    // Binder given to clients
    private final IBinder binder = new LocalBinder();
    // Registered callbacks
    private RouteServiceCallbacks serviceCallbacks;

    private RouteManager routeManager;

    // GoogleMapsApi
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "RouteService started!", Toast.LENGTH_SHORT).show();

        routeManager = RouteManager.getInstance();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Toast.makeText(RouteService.this, "Service detected loc change", Toast.LENGTH_SHORT).show();
                routeManager.add(locationResult);
                if (serviceCallbacks != null)
                    serviceCallbacks.updateMap();
            }
        };
        // Specify the sensitivity of the GPS sensor
        // @TODO change GPS sensitivities to more reasonable values after testing.
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5)
                .setFastestInterval(1)
                .setSmallestDisplacement(5)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "RouteService stopped!", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        public RouteService getService() {
            // Return this instance of MyService so clients can call public methods
            return RouteService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(RouteServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }
}
