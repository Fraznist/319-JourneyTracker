package com.example.eakgun14.journeytracker.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eakgun14.journeytracker.Adapters.AccelerationManagerAdapter;
import com.example.eakgun14.journeytracker.Adapters.LightManagerAdapter;
import com.example.eakgun14.journeytracker.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements SensorEventListener {

    private LightManagerAdapter lightManager;
    private AccelerationManagerAdapter accelManager;

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

        ViewGroup thisLayout = findViewById(R.id.settings_constraint_layout);
        lightManager = new LightManagerAdapter(thisLayout, this);
        accelManager = new AccelerationManagerAdapter(this, R.id.settings_horizontal_guideline, R.id.settings_vertical_guideline, (ConstraintLayout) thisLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lightManager.pause();
        accelManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lightManager.resume();
        accelManager.resume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightManager.illuminationChanged(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelManager.accelerationChanged(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
