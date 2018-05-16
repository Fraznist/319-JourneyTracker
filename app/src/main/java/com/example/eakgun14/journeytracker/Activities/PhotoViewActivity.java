package com.example.eakgun14.journeytracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.eakgun14.journeytracker.Adapters.PhotoGridAdapter;
import com.example.eakgun14.journeytracker.DataTypes.LatLngURIPair;
import com.example.eakgun14.journeytracker.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.List;

public class PhotoViewActivity extends AppCompatActivity {

    private File parentDirectory;
    private String photoOnEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        parentDirectory = this.getExternalFilesDir(Environment.DIRECTORY_DCIM);

        Intent intent = getIntent();
        String title = intent.getStringExtra("Journey Name");
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
