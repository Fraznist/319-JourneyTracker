package com.example.eakgun14.journeytracker.Adapters;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;

public class AccelerationManagerAdapter {

    private static final float MIN_MAGNITUDE = 0.5f;
    private static final float MAX_MAGNITUDE = 5;
    private float oldX = 0f;
    private float oldY = 0f;

    private SensorManager mSensorManager;
    private Sensor mAccelerationSensor;

    int horizontalID;
    int verticalID;
    ConstraintLayout thisLayout;
    ConstraintSet set;
    Context context;

    public AccelerationManagerAdapter(Context cont, int horid, int verid, ConstraintLayout layout) {
        context = cont;
        horizontalID = horid;
        verticalID = verid;
        thisLayout = layout;
        set = new ConstraintSet();
        set.clone(thisLayout);

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void pause() {
        mSensorManager.unregisterListener((SensorEventListener) context);
    }

    public void resume() {
        mSensorManager.registerListener((SensorEventListener) context, mAccelerationSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void accelerationChanged(SensorEvent event) {
        float x = setValue(event.values[0]);
        float y = setValue(event.values[1]);
        Log.d("stuff", "x: " + x + ", y: " + y);

        if (Math.abs(oldX - x) >= 0.1f) {
            float bias = ((x + MAX_MAGNITUDE) / (2 * MAX_MAGNITUDE) ) * -0.2f + 0.6f;
            set.setGuidelinePercent(verticalID, bias);
            oldX = x;
        }

        if (Math.abs(oldY - y) >= 0.1f) {
            float bias = ((y + MAX_MAGNITUDE) / (2 * MAX_MAGNITUDE) ) * 0.4f + 0.3f;
            set.setGuidelinePercent(horizontalID, bias);
            oldY = y;
        }

        set.applyTo(thisLayout);
    }

    private float setValue(float x) {
        if (Math.abs(x) <= MIN_MAGNITUDE) x = 0f;
        else if (x >= MAX_MAGNITUDE) x = MAX_MAGNITUDE;
        else if (x <= -MAX_MAGNITUDE) x = -MAX_MAGNITUDE;
        return x;
    }

    private boolean isBetweenBounds(float x) {
        float abs = Math.abs(x);
        return abs >= MIN_MAGNITUDE;
    }

}
