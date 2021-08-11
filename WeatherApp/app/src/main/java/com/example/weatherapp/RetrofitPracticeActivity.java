package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitPracticeActivity extends AppCompatActivity {
    Button testApiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_practice);
        init();
    }

    private void init() {
        testApiBtn = findViewById(R.id.testBtn);
        testApiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTheAPI();
            }
        });
    }

    private void callTheAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://em-it.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HerokuAppApi ha = retrofit.create(HerokuAppApi.class);

        Call<PersonModel> call = ha.getPersons();
        call.enqueue(new Callback<PersonModel>() {
            @Override
            public void onResponse(Call<PersonModel> call, Response<PersonModel> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RetrofitPracticeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    return;
                }
                PersonModel personData= response.body();
                System.out.println(personData.data.toString());
            }

            @Override
            public void onFailure(Call<PersonModel> call, Throwable t) {
                Toast.makeText(RetrofitPracticeActivity.this, "Error:" + t, Toast.LENGTH_SHORT).show();
              Log.e("api error",t.getLocalizedMessage());
              t.printStackTrace();
            }
        });
    }
}