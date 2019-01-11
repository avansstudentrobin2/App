package com.example.okker.app.network;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance1 {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://androidapi.goodyweb.nl";
    public static Retrofit getRetrofitInstance() {

        Gson gson = new GsonBuilder().setLenient().create();
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}