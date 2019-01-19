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
import com.example.okker.app.services.GetDataService;
import com.example.okker.app.adapter.SqLiteAdapter;
import com.example.okker.app.model.RetroPost;
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
    private RetroPost retroPost;
    private SwipeRefreshLayout swipeContainer;
    private List<RetroPost> allImagesList = new ArrayList<>();

    /**
     * Startup Activity from the app
     * Calls functions requestFineLocation & requestCoarseLocation for Location of user
     * Call to Api for all the RetroPosts and send to generateDataList function
     * @param savedInstanceState
     */
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

        Call<List<RetroPost>> call = service.getAllPhotos();
        call.enqueue(new Callback<List<RetroPost>>() {

            @Override
            public void onResponse(Call<List<RetroPost>> call, Response<List<RetroPost>> response) {
                progressDoalog.dismiss();
                Collections.reverse(response.body());
                generateDataList(response.body());
                SqLiteAdapter db = new SqLiteAdapter(MainActivity.this);
                db.addPosts(response.body());
            }

            @Override
            public void onFailure(Call<List<RetroPost>> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                SqLiteAdapter db = new SqLiteAdapter(MainActivity.this);
                List<RetroPost> photoList = db.getAllRetroPhotos();
                generateDataList(photoList);
            }
        });
    }

    /**
     * Method to refresh activity. Again call to Api to get data
     * @param page
     */
    public void fetchTimeLineAsync(int page) {
        adapter.clear();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<RetroPost>> call = service.getAllPhotos();
        call.enqueue(new Callback<List<RetroPost>>() {

            @Override
            public void onResponse(Call<List<RetroPost>> call, Response<List<RetroPost>> response) {
                progressDoalog.dismiss();
                Collections.reverse(response.body());
                generateDataList(response.body());
                SqLiteAdapter db = new SqLiteAdapter(MainActivity.this);
                db.addPosts(response.body());
            }

            @Override
            public void onFailure(Call<List<RetroPost>> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                //Get all posts from internal database
                SqLiteAdapter db = new SqLiteAdapter(MainActivity.this);
                List<RetroPost> photoList = db.getAllRetroPhotos();
                generateDataList(photoList);
            }
        });
        swipeContainer.setRefreshing(false);
    }

    /**
     * Method to generate List of data using RecyclerView with custom adapter
     * Send List to CustomAdapter
     * @param photoList
     */
    private void generateDataList(List<RetroPost> photoList) {
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

    /**
     * Ask user for permissions Fine & Coarse location
     * @param act
     */
    public static void requestFineLocation(Activity act) {
        ActivityCompat.requestPermissions(act,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public static void requestCoarseLocation(Activity act){
        ActivityCompat.requestPermissions(act,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    /**
     * Instantiate Searchbar, set onQueryListener
     * @param menu
     * @return
     */
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

    /**
     * Show result in recyclerview, based on searchquery
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        List<RetroPost> newList = new ArrayList<>();
        for(RetroPost item : allImagesList){
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

