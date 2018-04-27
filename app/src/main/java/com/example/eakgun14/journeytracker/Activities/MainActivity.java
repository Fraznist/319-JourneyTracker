package com.example.eakgun14.journeytracker.Activities;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eakgun14.journeytracker.Adapters.AccelerationManagerAdapter;
import com.example.eakgun14.journeytracker.Adapters.LightManagerAdapter;
import com.example.eakgun14.journeytracker.R;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private LightManagerAdapter lightManager;
    private AccelerationManagerAdapter accelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button manageJourneysButton = (Button) findViewById(R.id.manage_journey);
        manageJourneysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JournalsActivity.class);
                startActivity(intent);
            }
        });

        Button startJourneyButton = (Button) findViewById(R.id.start_journey);
        startJourneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StartJourneyActivity.class);
                startActivity(intent);
            }
        });

        Button settingsButton = (Button) findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        ViewGroup thisLayout = findViewById(R.id.main_constraint_layout);
        lightManager = new LightManagerAdapter(thisLayout, this);
        accelManager = new AccelerationManagerAdapter(this, R.id.main_horizontal_guideline, R.id.main_vertical_guideline, (ConstraintLayout) thisLayout);
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
