package com.example.weatherapp;


import android.app.Activity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrentWeatherService {
    private static final String TAG = CurrentWeatherService.class.getSimpleName();

    private static final String URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String CURRENT_WEATHER_TAG = "CURRENT_WEATHER";
    private static final String API_KEY = "429af3e13a8c6781bbaab32ed95aa343";

    private RequestQueue queue;

    public CurrentWeatherService(@NonNull final Activity activity) {
        queue = Volley.newRequestQueue(activity.getApplicationContext());
    }

    public interface CurrentWeatherCallback {
        @MainThread
        void onCurrentWeather(@NonNull final CurrentWeather currentWeather);

        @MainThread
        void onError(@NonNull Exception exception);

    }

    public void getCurrentWeather(@NonNull final String locationName, @NonNull final CurrentWeatherCallback currWCallback) {
        final String url = String.format("%s?q=%s&appId=%s", URL, locationName, API_KEY);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET
                , url
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject currWeatherJSONObj = new JSONObject(response);
                    final JSONArray weather = currWeatherJSONObj.getJSONArray("weather");
                    final JSONObject weatherCondition = weather.getJSONObject(0);
                    final String locationName = currWeatherJSONObj.getString("name");
                    final int conditionId = weatherCondition.getInt("id");
                    final String conditionName = weatherCondition.getString("main");
                    final double tempKelvin = currWeatherJSONObj.getJSONObject("main").getDouble("temp");
                    final CurrentWeather currWeather = new CurrentWeather(locationName, conditionId
                            , conditionName, tempKelvin);
                    currWCallback.onCurrentWeather(currWeather);
                } catch (JSONException e) {
                    currWCallback.onError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                currWCallback.onError(error);
            }
        });
        stringRequest.setTag(CURRENT_WEATHER_TAG);
        queue.add(stringRequest);
    }

    public void cancel() {
        queue.cancelAll(CURRENT_WEATHER_TAG);
    }
}
