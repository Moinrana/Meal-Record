package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WeatherAppActivity extends AppCompatActivity {
    private TextView tvTemperature, tvCity, tvWeatherType;
    private EditText etLocation;
    private View weatherContainer;
    private ProgressBar progressBar;
    private FloatingActionButton refreshBtn;
    private boolean textChanged = false;
    private int textCount = 0;
    private String currLocation = "Bangladesh";
    private CurrentWeatherService currWeatherService;
    private boolean fetchingWeather = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_app);
        init();
    }

    private void init() {
        currWeatherService = new CurrentWeatherService(this);
        tvTemperature = findViewById(R.id.temperature);
        tvCity = findViewById(R.id.location);
        tvWeatherType = findViewById(R.id.weatherType);
        etLocation = findViewById(R.id.locationText);
        weatherContainer = findViewById(R.id.mainContainer);
        progressBar = findViewById(R.id.progressBar);
        refreshBtn = findViewById(R.id.refreshBtn);

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textCount = charSequence.toString().trim().length();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textCount == 0) {
                    refreshWeather();
                } else {
                    searchForWeather(etLocation.getText().toString());
                    etLocation.setText("");
                }
            }
        });
        searchForWeather(currLocation);
    }

    private void refreshWeather() {
        if (fetchingWeather) {
            return;
        }
        searchForWeather(currLocation);
    }

    private void searchForWeather(@NonNull final String locationText) {
        toggleProgress(true);
        fetchingWeather = true;
        currWeatherService.getCurrentWeather(locationText, currentWeatherCallback);
    }

    private void toggleProgress(final boolean showProgress) {
        weatherContainer.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.GONE);
    }

    private final CurrentWeatherService.CurrentWeatherCallback currentWeatherCallback = new CurrentWeatherService.CurrentWeatherCallback() {
        @Override
        public void onCurrentWeather(@NonNull CurrentWeather currentWeather) {
            currLocation = currentWeather.location;
            tvTemperature.setText(String.valueOf(currentWeather.getTemperatureInCelcius()));
            tvCity.setText(currLocation);
            tvWeatherType.setText(currentWeather.weatherCondition);
            toggleProgress(false);
            fetchingWeather = false;
        }

        @Override
        public void onError(@NonNull Exception exception) {
            toggleProgress(false);
            fetchingWeather = false;
            Toast.makeText(WeatherAppActivity.this, "There was an error while fetching weather, try again", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        currWeatherService.cancel();
    }

}