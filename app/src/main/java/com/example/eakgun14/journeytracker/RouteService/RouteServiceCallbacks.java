package com.example.eakgun14.journeytracker.RouteService;

import com.example.eakgun14.journeytracker.DataTypes.WeatherInfo;

public interface RouteServiceCallbacks {
    void updateMap();
    void updateWeather(WeatherInfo weather);
}
