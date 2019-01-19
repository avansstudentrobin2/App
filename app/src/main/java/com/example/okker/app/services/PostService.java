package com.example.okker.app.services;

import android.content.Context;

import com.example.okker.app.model.RetroPost;
import com.example.okker.app.network.RetrofitClientInstance;
import com.example.okker.app.utils.FileUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


public class PostService {

    public void saveUserImage(Context context, String title, String place, String description, Double latitude, Double longitude, File imageFile, Callback<RetroPost> callback) {

        /**
         * Create upload service client
         */
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(FileUtils.getFileExtension(imageFile.getAbsolutePath())), imageFile);

        /**
         * MultipartBody.Part is used to send also the actual file name
         */
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("img", imageFile.getName(), requestFile);

        /**
         * Add another part within the multipart request
         */
        RequestBody titleName =
                RequestBody.create(
                        MultipartBody.FORM, title);

        RequestBody placeName =
                RequestBody.create(
                        MultipartBody.FORM, place);

        RequestBody descriptionName =
                RequestBody.create(
                        MultipartBody.FORM, description);

        RequestBody latitudeName =
                RequestBody.create(
                        MultipartBody.FORM, Double.toString(latitude));

        RequestBody longitudeName =
                RequestBody.create(
                        MultipartBody.FORM, Double.toString(longitude));

        Call<RetroPost> result =  service.uploadImage(titleName, placeName,descriptionName, latitudeName, longitudeName, body);

        result.enqueue(callback);

    }

}
