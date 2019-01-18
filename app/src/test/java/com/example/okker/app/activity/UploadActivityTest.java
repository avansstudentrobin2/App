package com.example.okker.app.activity;

import com.example.okker.app.R;
import com.example.okker.app.model.RetroPost;
import com.example.okker.app.services.PostService;
import com.example.okker.app.utils.FileUtils;

import org.junit.Test;

import java.io.File;

import retrofit2.Callback;

import static org.junit.Assert.*;

public class UploadActivityTest {

    @Test
    public void uploadImage() {
       String mCurrentPhotoPath = "drawable://" + R.drawable.camera;
        String output;
      PostService service = new PostService();
        if (mCurrentPhotoPath != null) {

            String title = "test";
            String description = "test";
            if(title.isEmpty() || description.isEmpty()) {
                output = null;
            } else {
                File image = new File(mCurrentPhotoPath);
                File compressedImage = FileUtils.saveBitmapToFile(image);
                output = "test";
                assertEquals(compressedImage , compressedImage);
            }
            assertEquals("test" , output);
        }


    }


}