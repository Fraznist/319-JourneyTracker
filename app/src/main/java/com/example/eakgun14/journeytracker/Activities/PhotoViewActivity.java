package com.example.eakgun14.journeytracker.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.eakgun14.journeytracker.Adapters.PhotoGridAdapter;
import com.example.eakgun14.journeytracker.DataTypes.LatLngURIPair;
import com.example.eakgun14.journeytracker.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Intent intent = getIntent();
        String title = intent.getStringExtra("Journey Name");
        String uriJSONList = intent.getStringExtra("URI JSON");

        Gson gson = new Gson();
        Log.d("nullUri", uriJSONList);
        List<LatLngURIPair> pairs = gson.fromJson(uriJSONList,
                new TypeToken<List<LatLngURIPair>>(){}.getType());

        List<Bitmap> bitmaps = new ArrayList<>();
        for (LatLngURIPair p : pairs) {
            String uri = p.getImageUri();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(uri));
                bitmaps.add(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PhotoGridAdapter adapter = new PhotoGridAdapter(this, bitmaps);

        GridView gridView = findViewById(R.id.photo_journey);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
