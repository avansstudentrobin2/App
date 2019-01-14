package com.example.okker.app.network;


import com.example.okker.app.adapter.GetDataService;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientWithInterceptor {

    public static final String BASE_URL = "https://androidapi.goodyweb.nl";

    private static Retrofit retrofit = null;

    public static OkHttpClient.Builder httpClient;

    public static Retrofit getClient(String baseUrl) {

        //http logging interceptor will give us the information about web service call response.
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient = new OkHttpClient.Builder();

        httpClient.readTimeout(60, TimeUnit.SECONDS);

        httpClient.connectTimeout(60, TimeUnit.SECONDS);

        httpClient.addInterceptor(logging);

        //We should add headers for the request.
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "multipart/form-data")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                .client(httpClient.build())
                .build();

        return retrofit;
    }


    public static GetDataService getMGClient() {

        return getClient(BASE_URL).create(GetDataService.class);
    }

}

