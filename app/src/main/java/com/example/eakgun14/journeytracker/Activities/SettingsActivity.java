package com.example.eakgun14.journeytracker.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.eakgun14.journeytracker.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button eng = findViewById(R.id.english_language);
        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration c = new Configuration(getResources().getConfiguration());
                c.locale = Locale.ENGLISH;
                getResources().updateConfiguration(c, getResources().getDisplayMetrics());
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button tr = findViewById(R.id.turkish_language);
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration c = new Configuration(getResources().getConfiguration());
                c.locale = new Locale("tr", "TR");
                getResources().updateConfiguration(c, getResources().getDisplayMetrics());
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
