package com.example.eakgun14.journeytracker.DataTypes;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherInfo {

    private int temperature;
    private String cityName;
    private String drawableName;

    public static WeatherInfo fromJSONObject(JSONObject json) {

        try {
            WeatherInfo weather = new WeatherInfo();

            weather.cityName = json.getString("name");

            double dTemp = json.getJSONObject("main").getDouble("temp") - 273.15;
            weather.temperature = (int) Math.rint(dTemp);

            int condition = json.getJSONArray("weather").getJSONObject(0).getInt("id");
            weather.drawableName = updateWeatherImage(condition);

            return weather;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String updateWeatherImage(int weatherId) {

        if (weatherId >= 0 && weatherId < 300) {
            return "tstorm1";
        } else if (weatherId >= 300 && weatherId < 500) {
            return "light_rain";
        } else if (weatherId >= 500 && weatherId < 600) {
            return "shower3";
        } else if (weatherId >= 600 && weatherId <= 700) {
            return "snow4";
        } else if (weatherId >= 701 && weatherId <= 771) {
            return "fog";
        } else if (weatherId >= 772 && weatherId < 800) {
            return "tstorm3";
        } else if (weatherId == 800) {
            return "sunny";
        } else if (weatherId >= 801 && weatherId <= 804) {
            return "cloudy2";
        } else if (weatherId >= 900 && weatherId <= 902) {
            return "tstorm3";
        } else if (weatherId == 903) {
            return "snow5";
        } else if (weatherId == 904) {
            return "sunny";
        } else if (weatherId >= 905 && weatherId <= 1000) {
            return "tstorm3";
        }

        return "sorry";
    }

    public String getTemperature() {
        return temperature + "°";
    }

    public String getCityName() {
        return cityName;
    }

    public String getDrawableName() {
        return drawableName;
    }
}
