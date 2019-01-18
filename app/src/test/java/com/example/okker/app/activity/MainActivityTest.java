package com.example.okker.app.activity;

import com.example.okker.app.model.RetroPost;
import com.example.okker.app.network.RetrofitClientInstance;
import com.example.okker.app.services.GetDataService;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;

public class MainActivityTest {


    String test;
    @Test
    public void onCreate() {
        //check retrofit
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<RetroPost>> call = service.getAllPhotos();
        try {
            Response<List<RetroPost>> response = call.execute();

            assertTrue(response.isSuccessful());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}