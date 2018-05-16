package com.example.eakgun14.journeytracker.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eakgun14.journeytracker.Adapters.PhotoGridAdapter;
import com.example.eakgun14.journeytracker.DataTypes.LatLngURIPair;
import com.example.eakgun14.journeytracker.R;
import com.example.eakgun14.journeytracker.RouteService.AudioManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.List;

public class PhotoViewActivity extends AppCompatActivity {

    private File parentDirectory;
    private String photoOnEdit = null;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        audioManager = new AudioManager(this.getApplicationContext());
        parentDirectory = this.getExternalFilesDir(Environment.DIRECTORY_DCIM);

        Intent intent = getIntent();
        final String title = intent.getStringExtra("Journey Name");
        String uriJSONList = intent.getStringExtra("URI JSON");

        TextView titus = findViewById(R.id.photo_view_title);
        titus.setText(title);

        Gson gson = new Gson();
        List<LatLngURIPair> pairs = gson.fromJson(uriJSONList,
                new TypeToken<List<LatLngURIPair>>(){}.getType());

        final PhotoGridAdapter adapter = new PhotoGridAdapter(this, pairs);

        GridView gridView = findViewById(R.id.photo_journey);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                photoOnEdit = adapter.getPairs().get(position).getImageUri();
                File photoFile = new File(parentDirectory, photoOnEdit);
                startEditor(photoFile);
            }
        });

        ImageButton playButton = findViewById(R.id.photo_record_play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAudioPermission();
                audioManager.onPlay(title);
            }
        });
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

    public static final int MY_PERMISSIONS_RECORD_AUDIO = 88;
    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // App doesn't have location services permission from OS

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the record audio permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(PhotoViewActivity.this,
                                        new String[]{Manifest.permission.RECORD_AUDIO},
                                        MY_PERMISSIONS_RECORD_AUDIO );
                            }
                        }).create().show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_RECORD_AUDIO );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break;
                }
                else finish();
            }
        }
    }
}
