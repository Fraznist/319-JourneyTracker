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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eakgun14.journeytracker.Adapters.PhotoGridAdapter;
import com.example.eakgun14.journeytracker.Adapters.ViewAdapterListener;
import com.example.eakgun14.journeytracker.DataTypes.LatLngNamePair;
import com.example.eakgun14.journeytracker.R;
import com.example.eakgun14.journeytracker.RouteService.AudioManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.List;

public class PhotoViewActivity extends AppCompatActivity
        implements ViewAdapterListener<LatLngNamePair> {

    private File parentDirectory;
    private String photoOnEdit = null;
    private AudioManager audioManager;
    private PhotoGridAdapter adapter;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        audioManager = AudioManager.getInstance();
        audioManager.setContext(this.getApplicationContext());
        parentDirectory = this.getExternalFilesDir(Environment.DIRECTORY_DCIM);

        Intent intent = getIntent();
        title = intent.getStringExtra("Journey Name");
        String uriJSONList = intent.getStringExtra("URI JSON");

        android.support.v7.widget.Toolbar bar = findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        ActionBar actBar = getSupportActionBar();
        assert actBar != null;
        actBar.setDisplayHomeAsUpEnabled(true);
        actBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actBar.setTitle(title);

        Gson gson = new Gson();
        List<LatLngNamePair> pairs = gson.fromJson(uriJSONList,
                new TypeToken<List<LatLngNamePair>>(){}.getType());

        adapter = new PhotoGridAdapter(this, this, pairs);

        final GridView gridView = findViewById(R.id.photo_journey);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play_menu, menu);
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                adapter.removeSelected();
                return true;
            case R.id.action_play:
                checkAudioPermission();
                audioManager.onPlay(title);
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onViewItemClicked(LatLngNamePair pair) {
        photoOnEdit = pair.getName();
        File photoFile = new File(parentDirectory, photoOnEdit);
        startEditor(photoFile);
    }
}
