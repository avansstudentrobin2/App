package com.example.okker.app.model;

import android.content.Intent;
import android.widget.Toast;

import com.example.okker.app.activity.MainActivity;
import com.example.okker.app.activity.ViewImageActivity;
import com.example.okker.app.adapter.SqLiteAdapter;
import com.example.okker.app.network.RetrofitClientInstance;
import com.example.okker.app.services.GetDataService;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;

public class RetroPostTest {

    @Test
    public void checkObjectCall() {
                RetroPost test = new RetroPost(1, "test", "test","test", 1.9, 1.9 ,"test","test","test");
                assertEquals(test, test);
            }

}
