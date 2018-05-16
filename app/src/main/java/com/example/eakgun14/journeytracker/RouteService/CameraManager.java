package com.example.eakgun14.journeytracker.RouteService;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.eakgun14.journeytracker.DataTypes.LatLngURIPair;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraManager {

    private static final CameraManager instance = new CameraManager();

    private List<LatLngURIPair> imageUriList = new ArrayList<>();
//    private Uri extPhotoUri;
    private String photoName;
    private Context ctx;

    public static CameraManager getInstance() {
        return instance;
    }

    public void setContext(Context c) {
        ctx = c;
    }

    public Uri setImageUri() {
        String timeStamp = new Date().toString();
        photoName = "IMG_" + timeStamp + ".jpg";
        File extPhoto = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_DCIM),  photoName);
        Uri extPhotoUri = Uri.fromFile(extPhoto);
        return extPhotoUri;
    }

    public void cacheImage(LatLng coords) {
        Log.d("saveloc", photoName/*extPhotoUri.toString()*/);
        imageUriList.add(new LatLngURIPair(coords, photoName/*extPhotoUri.toString()*/));
    }

    public List<LatLngURIPair> getImageUriList() {
        return imageUriList;
    }

    public void clear() {
//        extPhotoUri = null;
        photoName = null;
        imageUriList.clear();
    }
}
