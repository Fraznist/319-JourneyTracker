package com.example.eakgun14.journeytracker.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.ViewGroup;

import com.example.eakgun14.journeytracker.R;

public class LightManagerAdapter {

    final int MAX_RANGE = 2560;
    final int MIN_RANGE = 160;

    int lightColor;
    int darkColor;

    private Context context;
    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    protected ViewGroup thisLayout;

    public LightManagerAdapter(ViewGroup layout, Context cont) {
        context = cont;
        lightColor = context.getResources().getColor(R.color.colorPrimary);
        darkColor = context.getResources().getColor(R.color.colorPrimaryDark);
        thisLayout = layout;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    public void pause() {
        mSensorManager.unregisterListener((SensorEventListener) context);
    }

    public void resume() {
        mSensorManager.registerListener((SensorEventListener) context, mLightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void illuminationChanged(SensorEvent event) {
        float lumination = event.values[0];
        if (lumination > MAX_RANGE) lumination = MAX_RANGE;
        else if (lumination < MIN_RANGE) lumination = MIN_RANGE;

        int newColor = interpolateColor(darkColor, lightColor, lumination / (MAX_RANGE - MIN_RANGE));
        thisLayout.setBackgroundColor(newColor);
    }

    private float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }

    /** Returns an interpoloated color, between <code>a</code> and <code>b</code> */
    private int interpolateColor(int a, int b, float proportion) {
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        Color.colorToHSV(a, hsva);
        Color.colorToHSV(b, hsvb);
        for (int i = 0; i < 3; i++) {
            hsvb[i] = interpolate(hsva[i], hsvb[i], proportion);
        }
        return Color.HSVToColor(hsvb);
    }
}
