package com.example.eakgun14.journeytracker.Adapters;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;

// Custom class(probably inappropriately named)
// that handles input from the accelerometer in a certain manner.
public class AccelerationManagerAdapter {

    // input smaller in magnitude than MIN is set to 0
    // input bigger than max in magnitude is set to MAX
    private static final float MIN_MAGNITUDE = 0.5f;
    private static final float MAX_MAGNITUDE = 5;

    // state variables to check in order to act upon only significant changes in accelerometer
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

    // stop listening to accelerometer, called by activity.onPause
    public void pause() {
        mSensorManager.unregisterListener((SensorEventListener) context);
    }

    // start listening to accelerometer, called by activity.onResume
    public void resume() {
        mSensorManager.registerListener((SensorEventListener) context, mAccelerationSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void accelerationChanged(SensorEvent event) {
        float x = setValue(event.values[0]);
        float y = setValue(event.values[1]);

        // Move the components in the direction of the tilt in x dimension
        if (Math.abs(oldX - x) >= 0.1f) {
            float bias = ((x + MAX_MAGNITUDE) / (2 * MAX_MAGNITUDE) ) * -0.2f + 0.6f;
            set.setGuidelinePercent(verticalID, bias);
            oldX = x;
        }

        // Move the components in the direction of the tilt in y dimension
        if (Math.abs(oldY - y) >= 0.1f) {
            float bias = ((y + MAX_MAGNITUDE) / (2 * MAX_MAGNITUDE) ) * 0.4f + 0.3f;
            set.setGuidelinePercent(horizontalID, bias);
            oldY = y;
        }

        set.applyTo(thisLayout);
    }

    // Binds the magnitude of input to be smaller than MAX_MAGNITUDE
    // also sets it to 0 if it is smaller than MIN_MAGNITUDE
    private float setValue(float x) {
        if (Math.abs(x) <= MIN_MAGNITUDE) x = 0f;
        else if (x >= MAX_MAGNITUDE) x = MAX_MAGNITUDE;
        else if (x <= -MAX_MAGNITUDE) x = -MAX_MAGNITUDE;
        return x;
    }

}
