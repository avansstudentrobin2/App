package com.example.okker.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.okker.app.R;
import com.example.okker.app.services.GetDataService;
import com.example.okker.app.model.RetroPost;
import com.example.okker.app.network.RetrofitClientInstance;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.maps.GoogleMap;

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
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<RetroPost> call = service.getPhoto(imageId);
        call.enqueue(new Callback<RetroPost>() {

            @Override
            public void onResponse(Call<RetroPost> call, Response<RetroPost> response) {
                getDataFromCall(response.body());
            }

            @Override
            public void onFailure(Call<RetroPost> call, Throwable t) {
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

    private void getDataFromCall(RetroPost retroPost) {
        latitude = retroPost.getLatitude();
        longitude = retroPost.getLongitude();
        Picasso.get().load(retroPost.getImg())
                .placeholder((R.drawable.placeholder))
                .error(R.drawable.placeholder)
                .into(imageView);
        textViewTitle.setText(retroPost.getTitle());
        textViewText.setText(retroPost.getDescription());
        textViewPlace.setText(retroPost.getPlace());
        textViewCreatedDate.setText(retroPost.getCreated_at());
        textViewUpdatedDate.setText(retroPost.getUpdated_at());
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