package com.example.eakgun14.journeytracker.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eakgun14.journeytracker.DataTypes.LatLngNamePair;
import com.example.eakgun14.journeytracker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.File;

public class PhotoInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity act;

    public PhotoInfoWindowAdapter(Activity a) {
        act = a;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        @SuppressLint("InflateParams")
        View view = act.getLayoutInflater()
                .inflate(R.layout.info_window, null);

        TextView lat = view.findViewById(R.id.info_window_lat);
        TextView lng = view.findViewById(R.id.info_window_lng);
        ImageView img = view.findViewById(R.id.info_window_image);

        LatLngNamePair pair = (LatLngNamePair) marker.getTag();

        assert pair != null;
        File photo = new File(act.getExternalFilesDir(Environment.DIRECTORY_DCIM), pair.getName());
        BitmapWorkerTask.loadBitmapSync(photo, img);

        lat.setText(String.format("%s", pair.getCoords().latitude));
        lng.setText(String.format("%s", pair.getCoords().longitude));

        return view;
    }
}
