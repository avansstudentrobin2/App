package com.example.okker.app.utils;

import com.example.okker.app.R;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FileUtilsTest {
    String output;
    FileUtils fileutil = new FileUtils();

    @Test
    public void createImageFile() {
    }

    @Test
    public void getFileExtension() {
        String test ="test";
        output = fileutil.getFileExtension(test);
        assertTrue(output != null);

    }

    @Test
    public void saveBitmapToFile() {
        String imageUri = "drawable://" + R.drawable.camera; // A valid file path
        File file = new File(imageUri);
        fileutil.saveBitmapToFile(file);
        assertFalse(file == null);
    }
}