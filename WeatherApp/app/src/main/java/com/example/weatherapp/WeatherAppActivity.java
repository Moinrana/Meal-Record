package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WeatherAppActivity extends AppCompatActivity {
    private TextView tvTemperature, tvCity, tvWeatherType;
    private EditText etLocation;
    private View weatherContainer;
    private ProgressBar progressBar;
    private FloatingActionButton refreshBtn;
    private boolean textChanged = false;
    private int textCount = 0;
    private Button callApiBtn;
    private String currLocation = "Dhaka";
    private CurrentWeatherService currWeatherService;
    private boolean fetchingWeather = false;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    private static final String sharedPrefs = "last_location_data";
    private static final String spKey = "LAST_LOCATION_DATA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_app);
        storeDataInSP(currLocation);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentLocation();
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
        callApiBtn = findViewById(R.id.callApiBtn);
        // initialize fusedLocation
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        assignListeners();
    }

    private void assignListeners() {
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

        callApiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivity = new Intent(WeatherAppActivity.this,RetrofitPracticeActivity.class);
                startActivity(newActivity);
            }
        });


    }

    private void storeDataInSP(String place) {
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(spKey, place);
    }

    private String getFromSP() {
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
        String s = sharedPref.getString(spKey, "");
        if (s.trim().isEmpty()) {
            return "Chittagong";
        }
        return s;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(WeatherAppActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission granted


            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(60000);
            locationRequest.setFastestInterval(5000);
            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult == null) {
                        Log.e("loationRequest", "Location request is null");
                        currLocation = getFromSP();
                        searchForWeather(currLocation);
                    }
                    if (locationResult.getLocations() != null) {
                        Location location = locationResult.getLocations().get(0);
                        Geocoder geocoder = new Geocoder(WeatherAppActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            currLocation = addresses.get(0).getLocality().trim();
                            searchForWeather(currLocation);


                        } catch (IOException e) {
                            currLocation = getFromSP();
                            searchForWeather(currLocation);
                            e.printStackTrace();
                        }

                    }
                }
            };

            LocationServices.getFusedLocationProviderClient(WeatherAppActivity.this).requestLocationUpdates(locationRequest, locationCallback, null);

            LocationServices.getFusedLocationProviderClient(WeatherAppActivity.this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currLocation = getFromSP();
                    searchForWeather(currLocation);
                }
            });
        } else {
            //when permission not yet granted
            currLocation = getFromSP();
            searchForWeather(currLocation);
            ActivityCompat.requestPermissions(WeatherAppActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
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