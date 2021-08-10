package com.example.weatherapp;

public class CurrentWeatherUtils {

    public static int getWeatherIconResID(final int weatherConditionId) {
        switch (weatherConditionId) {
            case 200:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                return R.drawable.ic_sunny;
            default:return  R.drawable.ic_sunny;
        }
    }
}
