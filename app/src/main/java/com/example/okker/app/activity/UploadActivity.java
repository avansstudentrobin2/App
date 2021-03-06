package com.example.okker.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.okker.app.R;
import com.example.okker.app.model.RetroPost;
import com.example.okker.app.services.PostService;
import com.example.okker.app.utils.FileUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UploadActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public static String TAG = UploadActivity.class.getSimpleName();
    ImageView mImageView;
    static final int REQUEST_IMAGE_CAPTURE = 1000;
    PostService service;
    String mCurrentPhotoPath;
    ProgressDialog pDialog;
    TextInputEditText descriptionText, titleText;
    private LocationRequest mLocationRequest;
    private GoogleApiClient googleApiClient;
    private Double lat;
    private Double lon;
    private String userLocation;
    TextView textViewCity;

    /**
     * Activity for uploading new RetroPost
     * Call function getUserLocation to receive the location of user
      * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadactivity_layout);
        getUserLocation();
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(true);
        service = new PostService();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        textViewCity = (TextView) findViewById(R.id.cityCurrentText);
        mImageView = (ImageView) findViewById(R.id.imageView);
        descriptionText = (TextInputEditText) findViewById(R.id.descriptionText);
        titleText = (TextInputEditText) findViewById(R.id.titleText);

        Picasso.get()
                .load(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(mImageView);
    }

    /**
     * Start intent with camera and put content to activity after the camera
     * @param view
     */
    public void selectImage(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {

                photoFile = FileUtils.createImageFile(this);
                mCurrentPhotoPath = photoFile.getAbsolutePath();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                //getUriForFile, which returns a content:// URI. For more recent apps targeting Android 7.0 (API level 24) and higher,
                //don't forget to change your package name
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.okker.app.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Get data from camera intent with checks on nulls
     * Show image in ImageView
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode " + requestCode);
        Log.d(TAG, "onActivityResult: resultCode == RESULT_OK ? " + (resultCode == RESULT_OK));
        if ( requestCode == REQUEST_IMAGE_CAPTURE  && resultCode == RESULT_OK) {

            try {
                Picasso.get().load("file:" + mCurrentPhotoPath).into(mImageView);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED) {
            mCurrentPhotoPath = null;
        }

    }

    /**
     * This method uploads the view to the Api with retrofit
     * @param view
     */
    public void uploadImage(View view) {
        try {
            Log.d(TAG, "LOGGING: uploadImage: mCurrentPhotoPath " + mCurrentPhotoPath);
            if (mCurrentPhotoPath != null) {
                pDialog.show();
                String title = titleText.getText().toString();
                String description = descriptionText.getText().toString();
                if(title.isEmpty() || description.isEmpty()) {
                    showMessage("Not all the data has been filled, try again");
                    pDialog.hide();
                } else {

                    File image = new File(mCurrentPhotoPath);
                   File compressedImage = FileUtils.saveBitmapToFile(image);

                    service.saveUserImage(this, title, userLocation, description, lat, lon, compressedImage, new Callback<RetroPost>() {
                        @Override
                        public void onResponse(Call<RetroPost> call, Response<RetroPost> response) {
                            showMessage("Sale has been successfully uploaded");
                            pDialog.hide();
                        }

                        @Override
                        public void onFailure(Call<RetroPost> call, Throwable t) {
                            showMessage("Upload has Failed!");
                            pDialog.hide();
                        }
                    });
                }
            } else {
                showMessage("Image has not been taken yet, please try again by pressing the camera button");
                pDialog.hide();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showMessage(String message) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(UploadActivity.this).create();
            alertDialog.setTitle("Message");
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get location of user, if Android give permission to location
     * No perrmission -> Go back to main activity
     * GoogleApiClient build location
     */
    public void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                requestFineLocation(this);
                requestCoarseLocation(this);
            }
            boolean resultFineLocation = checkFineLocation(this);
            boolean resultCoarseLocation = checkCoarseLocation(this);
            if(!resultFineLocation && !resultCoarseLocation){
                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
        UploadActivity Context = this;
        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    /**
     * Location onStart connect()
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    /**
     * Location onStop disconnect()
     */
    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    /**
     * On googleApiClient connection get latest found location and set Latitude & Longitude
     * Geocode the city of the user
     * @param bundle
     */
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Check whether location settings are satisfied
            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            settingsClient.checkLocationSettings(locationSettingsRequest);
            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            final FusedLocationProviderClient client =
                    LocationServices.getFusedLocationProviderClient(this);
            //Get user last location
            client.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                //Latitude and longtitude are the coordinates for Google Maps
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                try {
                                    Geocoder geocoder = new Geocoder(UploadActivity.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                                    int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();
                                    userLocation = addresses.get(0).getAddressLine(maxAddressLine);
                                    textViewCity.setText(userLocation = addresses.get(0).getLocality());
                                } catch (Exception ex) {
                                    Log.v(ex.toString(), ex.toString());
                                }
                            }
                        }
                    });
        }
    }

    public void onConnectionSuspended(int i) {

    }

    /**
     * RequestPermissions for location
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
     * CheckPermissions for location
     * @param act
     * @return
     */
    public static boolean checkFineLocation(Activity act)
    {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkCoarseLocation(Activity act)
    {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

