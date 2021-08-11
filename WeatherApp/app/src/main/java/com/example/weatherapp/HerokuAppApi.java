package com.example.weatherapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HerokuAppApi {
    @GET("api/person")
    Call<PersonModel> getPersons();
}
