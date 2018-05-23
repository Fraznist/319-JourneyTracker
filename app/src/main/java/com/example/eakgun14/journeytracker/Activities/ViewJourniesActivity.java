package com.example.eakgun14.journeytracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.eakgun14.journeytracker.Adapters.PhotoInfoWindowAdapter;
import com.example.eakgun14.journeytracker.DataTypes.LatLngNamePair;
import com.example.eakgun14.journeytracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ViewJourniesActivity extends FragmentActivity implements OnMapReadyCallback,
        OnMapLoadedCallback, OnInfoWindowClickListener, OnMarkerClickListener {

    private GoogleMap mMap;
    private List<List<LatLng>> routeList;
    private List<List<LatLngNamePair>> pairListList;
    private File parentDirectory;
    private String photoOnEdit = null;

    private static final int[] colors =
            {Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN, Color.MAGENTA, Color.CYAN};
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journies);

        parentDirectory = this.getExternalFilesDir(Environment.DIRECTORY_DCIM);

        Intent intent = getIntent();
        String[] routes = intent.getStringArrayExtra("routes");
        String[] pairs = intent.getStringArrayExtra("pairs");

        // Randomize circular array starting index to add variables
        Random rand = new Random();
        i = rand.nextInt(6);

        routeList = new LinkedList<>();
        pairListList = new LinkedList<>();

        // deserialize every JSON file into a list of LatLng coordinates
        Gson gson = new Gson();
        for (String route : routes) {
            List<LatLng> latLngList = gson.fromJson(route,
                    new TypeToken<List<LatLng>>(){}.getType());
            Log.d("route", "size of route: " + latLngList.size());
            routeList.add(latLngList);
        }

        for (String pair1 : pairs) {
            List<LatLngNamePair> pair = gson.fromJson(pair1,
                    new TypeToken<List<LatLngNamePair>>(){}.getType());
            pairListList.add(pair);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        // Find out the bounding rectangle that surrounds all of the LatLng coordinates
        // that have to be displayed, then zoom the camera accordingly.
        int x = 0;  // Counter, newLatLngBounds raises an exception when there aren't any points
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < routeList.size(); i++) {
            List<LatLng> route = routeList.get(i);
            List<LatLngNamePair> pairList = pairListList.get(i);
            // Find the bounding latitude and longitude values, to determine where to center the camera
            for (LatLng latlng : route) {
                x++;
                builder.include(latlng);
            }

            int color = getNextColor();
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);

            for (LatLngNamePair pair : pairList) {
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(pair.getCoords())
                        .icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));
                m.setTag(pair);
            }

            PolylineOptions polyLine = new PolylineOptions()
                    .addAll(route)
                    .color(color)
                    .width(10.0f);
            mMap.addPolyline(polyLine);
        }
        if (x == 0) {
            // Don't try to
            Toast.makeText(this, "No points to display", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLngBounds bounds = builder.build();
        mMap.setInfoWindowAdapter(new PhotoInfoWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(this);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    private int getNextColor() {
        // Use the color array in a circular manner.
        int nextColor = colors[i];
        i++; i = i % colors.length;
        return nextColor;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();
        return marker.isInfoWindowShown();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d("as","click");
        photoOnEdit = ((LatLngNamePair)marker.getTag()).getName();
        File photoFile = new File(parentDirectory, photoOnEdit);
        startEditor(photoFile);
    }

    private static final int EDIT_RESULT_CODE = 1010;
    public void startEditor(File file) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(Uri.fromFile(file), "image/*");
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, EDIT_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_RESULT_CODE :
                if (resultCode == Activity.RESULT_OK && photoOnEdit != null)
                    overwriteOldPicture(new File(parentDirectory, photoOnEdit));
        }
    }

    private void overwriteOldPicture(File sourceFile) {
        String sourceName = sourceFile.getName();

        int i = sourceName.lastIndexOf('.');
        String editedName = sourceName.substring(0, i) + "_1" + sourceName.substring(i);

        File editedPhoto = new File(parentDirectory, editedName);

        if (editedPhoto.renameTo(sourceFile))
            Log.d("rename", "YE BOIII");
        else
            Log.d("rename", "no boi :(");
    }
}
