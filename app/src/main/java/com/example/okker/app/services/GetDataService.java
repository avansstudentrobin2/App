package com.example.okker.app.services;

import com.example.okker.app.model.RetroPhoto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface GetDataService {

    @GET("/api/posts")
    Call<List<RetroPhoto>> getAllPhotos();

    @GET("/api/posts/{id}")
    Call<RetroPhoto> getPhoto(@Path("id") Integer id);

    @Multipart
    @POST("/api/posts")
    Call<RetroPhoto> uploadImage(@Part("title") RequestBody title,
                                 @Part("place") RequestBody place,
                                 @Part("description") RequestBody description,
                                 @Part("latitude") RequestBody latitude,
                                 @Part("longitude") RequestBody longitude,
                                 @Part MultipartBody.Part file);

}