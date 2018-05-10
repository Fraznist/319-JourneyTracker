package com.example.eakgun14.journeytracker.RouteService;

import com.example.eakgun14.journeytracker.DataTypes.WeatherInfo;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class WeatherManager {
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String APP_ID = "e72ca729af228beabd5d20e3b7749713";

    private static final WeatherManager instance = new WeatherManager();
    private List<WeatherListener> subscribers = new LinkedList<>();

    private LatLng oldLoc = null;

    public static WeatherManager getInstance() {
        return instance;
    }

    public void locationChanged(LatLng coordinates) {
        // Request Weather update from the API only if the activity has just started,
        // or the device had a dispalcement of at least 10km since last weather update
        if (oldLoc == null) {
            oldLoc = coordinates;
            getLatLngWeather(coordinates);
        }
        else if (distance(oldLoc, coordinates) > 10) {
            oldLoc = coordinates;
            getLatLngWeather(coordinates);
        }
    }

    private void getLatLngWeather(LatLng coordinates) {
        RequestParams params = new RequestParams();
        params.put("lat", coordinates.latitude);
        params.put("lon", coordinates.longitude);
        params.put("appid", APP_ID);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                WeatherInfo weatherData = WeatherInfo.fromJSONObject(response);
                for (WeatherListener l : subscribers)
                    l.onWeatherInfo(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
//                Toast.makeText(StartJourneyActivity.this,
//                        "HTTP Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // calculate the distance between 2 LatLng points, using the Haversine Method without elevation
    private static double distance(LatLng coord1, LatLng coord2) {

        double lat1 = coord1.latitude, lon1 = coord1.longitude;
        double lat2 = coord2.latitude, lon2 = coord2.longitude;

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public void addSubscriber(WeatherListener listener) {
        subscribers.add(listener);
    }

}
