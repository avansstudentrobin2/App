package com.example.okker.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.okker.app.R;
import com.example.okker.app.adapter.GetDataService;
import com.example.okker.app.model.RetroPhoto;
import com.example.okker.app.network.RetrofitClientInstance;
import com.example.okker.app.network.RetrofitClientInstance1;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.maps.GoogleMap;

import java.util.Map;

public class ViewImageActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageView imageView;
    TextView textViewTitle, textViewText, textViewPlace, textViewCreatedDate, textViewUpdatedDate;
    Button routeButton;
    private Double latitude, longitude;

    GoogleMap mGoogleMap;
    MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image_layout);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewText = (TextView) findViewById(R.id.textViewText);
        imageView = (ImageView) findViewById(R.id.imageView);
        routeButton = (Button) findViewById(R.id.routeButton);
        mMapView = (MapView) findViewById(R.id.mapGoogleView);
        textViewPlace = (TextView) findViewById(R.id.textViewPlace);
        textViewCreatedDate = (TextView) findViewById(R.id.textViewCreatedDate);
        textViewUpdatedDate = (TextView) findViewById(R.id.textViewUpdatedDate);

        //Get imageview ID
        Integer imageId = Integer.parseInt(getIntent().getExtras().getString("id"));

        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance1.getRetrofitInstance().create(GetDataService.class);

        Call<RetroPhoto> call = service.getPhoto(imageId);
        call.enqueue(new Callback<RetroPhoto>() {

            @Override
            public void onResponse(Call<RetroPhoto> call, Response<RetroPhoto> response) {
                getDataFromCall(response.body());
            }

            @Override
            public void onFailure(Call<RetroPhoto> call, Throwable t) {
                Intent goMainActivity = new Intent(ViewImageActivity.this, MainActivity.class);
                startActivity(goMainActivity);
            }
        });

        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }


    private void getDataFromCall(RetroPhoto retroPhoto) {
        latitude = retroPhoto.getLatitude();
        longitude = retroPhoto.getLongitude();
        Picasso.Builder builder = new Picasso.Builder(ViewImageActivity.this);
        builder.downloader(new OkHttp3Downloader(ViewImageActivity.this));
        builder.build().load(retroPhoto.getImg())
                .placeholder((R.drawable.ic_launcher_background))
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
        textViewTitle.setText(retroPhoto.getTitle());
        textViewText.setText(retroPhoto.getDescription());
        textViewPlace.setText(retroPhoto.getPlace());
        textViewCreatedDate.setText(retroPhoto.getCreated_at());
        textViewUpdatedDate.setText(retroPhoto.getUpdated_at());
        setLocationOnMap();
    }

    private void setLocationOnMap(){
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        mGoogleMap = googleMap;
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng pos = new LatLng(latitude, longitude);
        mGoogleMap.addMarker(new MarkerOptions().position(pos));
        CameraPosition imagePosition = CameraPosition.builder().target(pos).zoom(12).bearing(4).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(imagePosition));
    }
}