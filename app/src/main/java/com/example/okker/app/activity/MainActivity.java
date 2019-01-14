package com.example.okker.app.activity;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.okker.app.R;
import com.example.okker.app.adapter.CustomAdapter;
import com.example.okker.app.adapter.GetDataService;
import com.example.okker.app.model.RetroPhoto;
import com.example.okker.app.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private CustomAdapter adapter;
    private RecyclerView recyclerView;
    ProgressDialog progressDoalog;
    private RetroPhoto retroPhoto;
    private SwipeRefreshLayout swipeContainer;
    private List<RetroPhoto> allImagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimeLineAsync(0);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        requestFineLocation(this);
        requestCoarseLocation(this);
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();

        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<RetroPhoto>> call = service.getAllPhotos();
        call.enqueue(new Callback<List<RetroPhoto>>() {

            @Override
            public void onResponse(Call<List<RetroPhoto>> call, Response<List<RetroPhoto>> response) {
                progressDoalog.dismiss();
                Collections.reverse(response.body());
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<RetroPhoto>> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method to refresh activity
    public void fetchTimeLineAsync(int page) {
        //AsyncHttpClient client = new AsyncHttpClient();
        adapter.clear();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        //GetDataService service = ClientWithInterceptor.getMGClient();

        Call<List<RetroPhoto>> call = service.getAllPhotos();
        call.enqueue(new Callback<List<RetroPhoto>>() {

            @Override
            public void onResponse(Call<List<RetroPhoto>> call, Response<List<RetroPhoto>> response) {
                progressDoalog.dismiss();
                Collections.reverse(response.body());
                generateDataList(response.body());
                //Set all responseitem in the local storage of your phone
                for(RetroPhoto item : response.body()) {

                }
            }

            @Override
            public void onFailure(Call<List<RetroPhoto>> call, Throwable t) {
                //Check in local storage of the phone on posts
                boolean checkOnLocalStorage = getLocalStoragePosts();
                if(checkOnLocalStorage == false) {
                    progressDoalog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDoalog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong with your internet connection. We fill your timeline with existing items.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        swipeContainer.setRefreshing(false);
    }

    private boolean getLocalStoragePosts() {
        //Get SqlLite database and fill the Activity
        return false;
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<RetroPhoto> photoList) {
        allImagesList = photoList;
        recyclerView = findViewById(R.id.customRecyclerView);
        adapter = new CustomAdapter(this,photoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void Clickt(View view)
    {
        Intent intent = new Intent(MainActivity.this, UploadActivity.class);
        startActivity(intent);
    }

    //Ask user for permissions Fine & Coarse location
    public static void requestFineLocation(Activity act) {
        ActivityCompat.requestPermissions(act,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public static void requestCoarseLocation(Activity act){
        ActivityCompat.requestPermissions(act,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        List<RetroPhoto> newList = new ArrayList<>();
        for(RetroPhoto item : allImagesList){
            if(item.getTitle().toLowerCase().contains(userInput)) {
                if(!newList.contains(item)) {
                    newList.add(item);
                }
            }
            if(item.getPlace().toLowerCase().contains(userInput)) {
                if(!newList.contains(item)) {
                    newList.add(item);
                }
            }
        }
        adapter.updateList(newList);
        return true;
    }
}

