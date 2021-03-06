package com.example.eakgun14.journeytracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.eakgun14.journeytracker.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar bar = findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        ActionBar actBar = getSupportActionBar();
        assert actBar != null;
        actBar.setDisplayHomeAsUpEnabled(true);
        actBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actBar.setTitle("Journey Tracker");

        Button manageJourneysButton = findViewById(R.id.manage_journey);
        manageJourneysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JournalsActivity.class);
                startActivity(intent);
            }
        });

        Button startJourneyButton = findViewById(R.id.start_journey);
        startJourneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StartJourneyActivity.class);
                startActivity(intent);
            }
        });

        Button settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
