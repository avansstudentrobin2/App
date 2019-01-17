package com.example.okker.app.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.okker.app.model.RetroPost;

import java.util.LinkedList;
import java.util.List;

public class SqLiteAdapter extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RetroPhotos";
    // Contacts table name
    private static final String TABLE_POSTS = "posts";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PLACE = "place";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_IMG = "img";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UPDATED_AT = "updated_at";
    public static final String CREATE_RETROPHOTO_TABLE = "CREATE TABLE " + TABLE_POSTS + " ("
            + KEY_ID + " INT,"
            + KEY_TITLE + " TEXT,"
            + KEY_PLACE + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_LONGITUDE + " FLOAT,"
            + KEY_LATITUDE + " FLOAT,"
            + KEY_IMG + " TEXT,"
            + KEY_CREATED_AT + " TEXT,"
            + KEY_UPDATED_AT + " TEXT" + ")";

    public SqLiteAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table
        db.execSQL(CREATE_RETROPHOTO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("LOGGER", "DROP TABLE");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        // Creating tables again
        onCreate(db);
    }

    public void addPosts(List<RetroPost> retroPost) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Drop old table
        db.execSQL("DROP TABLE " + TABLE_POSTS);
        //Create new table with right values
        db.execSQL(CREATE_RETROPHOTO_TABLE);
        //Set retrophoto values and insert into sqlite database
        ContentValues values = new ContentValues();
        for(RetroPost photo : retroPost) {
            values.put(KEY_ID, photo.getId());
            values.put(KEY_TITLE, photo.getTitle());
            values.put(KEY_PLACE, photo.getPlace());
            values.put(KEY_DESCRIPTION, photo.getDescription());
            values.put(KEY_LONGITUDE, photo.getLongitude());
            values.put(KEY_LATITUDE, photo.getLatitude());
            values.put(KEY_IMG, photo.getImg());
            values.put(KEY_CREATED_AT, photo.getCreated_at());
            values.put(KEY_UPDATED_AT, photo.getUpdated_at());
            db.insert(TABLE_POSTS, null, values);
        }
        db.close();
    }

    public List<RetroPost> getAllRetroPhotos() {
        //Make list of retrophotos and fill from table TABLE_POSTS in DB
        List<RetroPost> allRetroPosts = new LinkedList<RetroPost>();
        String query = "SELECT  * FROM " + TABLE_POSTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        RetroPost retroPost = null;
        //Loop retrophotos and return list
        if (cursor.moveToFirst()) {
            do {
                retroPost = new RetroPost(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2),cursor.getString(3),Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5)),cursor.getString(6),cursor.getString(7),cursor.getString(8));
                // Add retrophoto to list of retrophotos
                allRetroPosts.add(retroPost);
            } while (cursor.moveToNext());
        }
        return allRetroPosts;
    }
}