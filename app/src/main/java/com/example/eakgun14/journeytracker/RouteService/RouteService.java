package com.example.eakgun14.journeytracker.RouteService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.example.eakgun14.journeytracker.DataTypes.WeatherInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteService extends Service implements WeatherListener {
    // Binder given to clients
    private final IBinder binder = new LocalBinder();
    // Registered callbacks
    private RouteServiceCallbacks serviceCallbacks;
    public static boolean serviceRunning = false;

    private RouteManager routeManager;
    private WeatherManager weatherManager;

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "RouteService started!", Toast.LENGTH_SHORT).show();

        routeManager = RouteManager.getInstance();
        weatherManager = WeatherManager.getInstance();
        weatherManager.addSubscriber(this);

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LatLng coordinates = getLatLng(locationResult);
                routeManager.add(coordinates);
                if (serviceCallbacks != null)
                    serviceCallbacks.updateMap();
                if (serviceCallbacks != null)
                    weatherManager.locationChanged(coordinates);
            }
        };
        // Specify the sensitivity of the GPS sensor
        // @TODO change GPS sensitivities to more reasonable values after testing.
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000)
                .setFastestInterval(5000)
                .setSmallestDisplacement(20)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        serviceRunning = true;

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "RouteService stopped!", Toast.LENGTH_SHORT).show();
        serviceRunning = false;
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

    private LatLng getLatLng(LocationResult locationResult) {
        List<Location> locationList = locationResult.getLocations();
        if (locationList.size() > 0) {
            //The last location in the list is the newest
            Location location = locationList.get(locationList.size() - 1);
            // Only concerned about coordinates
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        else return null;
    }

    @Override
    public void onWeatherInfo(WeatherInfo weather) {
        if (serviceCallbacks != null)
            serviceCallbacks.updateWeather(weather);
    }
}
