package com.example.weatherapp;

public class CurrentWeather {
    final String location;
    final int conditionId;
    final String weatherCondition;
    final double tempKelvin;

    public CurrentWeather(String l, int cId, String wc, double tk) {
        this.location = l;
        this.conditionId = cId;
        this.weatherCondition = wc;
        this.tempKelvin = tk;
    }

    public int getTemperatureInCelcius() {
        return (int) (this.tempKelvin - 273);
    }

}
