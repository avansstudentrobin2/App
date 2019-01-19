package com.example.okker.app.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://androidapi.goodyweb.nl";

    /**
     * Build a new retrofit instance with the url and convert to gson
     * @return retrofit
     */
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
