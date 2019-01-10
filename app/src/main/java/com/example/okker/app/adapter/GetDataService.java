package com.example.okker.app.adapter;

import com.example.okker.app.model.RetroPhoto;
import com.example.okker.app.model.ServerResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface GetDataService {

    @GET("/api/posts")
    Call<List<RetroPhoto>> getAllPhotos();

    @POST("/api/posts")
    @Multipart
    Call<ResponseBody> uploadFile(@Part("title") RequestBody title,
                                  @Part ("body") RequestBody body,
                                  @Part MultipartBody.Part imageFile);

    @GET("/api/posts/{id}")
    Call<RetroPhoto> getPhoto(@Path("id") Integer id);
}