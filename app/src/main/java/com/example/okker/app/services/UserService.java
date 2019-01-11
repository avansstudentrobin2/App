package com.example.okker.app.services;

import android.content.Context;

import com.example.okker.app.adapter.GetDataService;
import com.example.okker.app.model.RetroPhoto;
import com.example.okker.app.network.RetrofitClientInstance;
import com.example.okker.app.utils.FileUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


public class UserService {

    public void saveUserImage(Context context, String title, String place, String description, Double latitude, Double longitude, File imageFile, Callback<RetroPhoto> callback) {

        // create upload service client
        GetDataService service = RetrofitClientInstance.getMGClient();

        RequestBody requestFile =
                RequestBody.create(MediaType.parse(FileUtils.getFileExtension(imageFile.getAbsolutePath())), imageFile);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("img", imageFile.getName(), requestFile);

        // add another part within the multipart request
       //String title1 = title;
        RequestBody titleName =
                RequestBody.create(
                        MultipartBody.FORM, title);

        //String place1 = place;
        RequestBody placeName =
                RequestBody.create(
                        MultipartBody.FORM, place);

       // String description1 = description;
        RequestBody descriptionName =
                RequestBody.create(
                        MultipartBody.FORM, description);

       //Double latitude1 = latitude;
        RequestBody latitudeName =
                RequestBody.create(
                        MultipartBody.FORM, Double.toString(latitude));

       //String longitude1 = longitude;
        RequestBody longitudeName =
                RequestBody.create(
                        MultipartBody.FORM, Double.toString(longitude));

        Call<RetroPhoto> result =  service.uploadImage(titleName, placeName,descriptionName, latitudeName, longitudeName, body);

        result.enqueue(callback);

    }

}
