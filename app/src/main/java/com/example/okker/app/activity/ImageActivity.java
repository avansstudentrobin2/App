package com.example.okker.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.telecom.DisconnectCause;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.okker.app.BuildConfig;
import com.example.okker.app.R;
import com.example.okker.app.adapter.GetDataService;
import com.example.okker.app.model.ServerResponse;
import com.example.okker.app.network.RetrofitClientInstance;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.UserDictionary.Words.APP_ID;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


//public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
//public abstract class ImageActivity extends AppCompatActivity implements View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {
public class ImageActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ImageView imageView;
    Button pickImage, upload;
    TextView textViewCity;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private Uri mMediaUri;
    private static final int CAMERA_PIC_REQUEST = 1111;

    private static final String TAG = ImageActivity.class.getSimpleName();

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;

    private Uri fileUri;

    private String mediaPath;

    private Button btnCapturePicture;

    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    ProgressDialog pDialog;
    private String postPath;

    private GoogleApiClient googleApiClient;
    private Double lat;
    private Double lon;
    private String userLocation;
    TextView textViewLat;
    TextView textViewLong;
    //private FusedLocationProviderClient FusedLocationClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);

        imageView = (ImageView) findViewById(R.id.preview);
        pickImage = (Button) findViewById(R.id.pickImage);
        upload = (Button) findViewById(R.id.upload);
        textViewCity = (TextView) findViewById(R.id.city);

        File file = new File("/storage/emulated/0/Download/IMG_7096.jpg");
        // create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://android.goodyweb.nl")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // create api instance
        GetDataService api = retrofit.create(GetDataService.class);
        // create call object
        Call<ResponseBody> uploadFileCall = api.uploadFile(
                RequestBody.create(MediaType.parse("text/plain"), "title"),
                RequestBody.create(MediaType.parse("text/plain"), "body"),
                MultipartBody.Part.createFormData(
                        "img",
                        file.getName(),
                        RequestBody.create(MediaType.parse("image"), file))

        );
// sync call
        /*try
        {
            ResponseBody responseBody = uploadFileCall.execute().body();
        } catch (Exception e)
        {
            e.printStackTrace();
        }*/
// async call
        uploadFileCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // TODO
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO
            }
        });

        getUserLocation();
    }

    public void onClick(View v) {

    }

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
                Intent intent = new Intent(ImageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
        ImageActivity Context = this;
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

    //Location onStart
    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    //Location onStop
    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    //Location Connected
    @Override
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
                                    Geocoder geocoder = new Geocoder(ImageActivity.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                                    int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();
                                    String cityName = addresses.get(0).getAddressLine(maxAddressLine);
                                    textViewCity.setText(cityName = addresses.get(0).getLocality());
                                } catch (Exception ex) {
                                    Log.v(ex.toString(), ex.toString());
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    //RequestPermissions
    public static void requestFineLocation(Activity act) {
        ActivityCompat.requestPermissions(act,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public static void requestCoarseLocation(Activity act){
        ActivityCompat.requestPermissions(act,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    //CheckPermissions
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
