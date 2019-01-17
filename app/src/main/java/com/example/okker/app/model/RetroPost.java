package com.example.okker.app.model;

import com.google.gson.annotations.SerializedName;

public class RetroPost {

    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("place")
    private String place;
    @SerializedName("description")
    private String description;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("img")
    private String img;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("updated_at")
    private String updated_at;

    public RetroPost(Integer id, String title, String place, String description, Double longitude, Double latitude, String img, String created_at, String updated_at) {
        this.id = id;
        this.title = title;
        this.place = place;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.img = img;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getPlace() { return place; }

    public void setPlace(String place) { this.place = place; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Double getLongitude() { return longitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public String getImg() { return img; }

    public void setImg(String img) { this.img = img; }

    public String getCreated_at() { return created_at; }

    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getUpdated_at() { return updated_at; }

    public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

}