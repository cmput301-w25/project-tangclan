package com.example.tangclan;

import static com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils.getPath;
import static com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EncodingUtils.getBytes;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageValidator {

    private static final long MAX_IMAGE_SIZE = 65536;

    public static boolean isValidImageSize(Context context, Uri imageUri) {

        if (imageUri == null) return false; //if no image is selected

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri); //converts image to raw data
            byte[] imageBytes = getBytes(inputStream); //stream into byte array

            if (imageBytes.length > MAX_IMAGE_SIZE) { //checks image size
                Toast.makeText(context, "Image is too large! Please select an image under 65536 bytes.", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        } catch (IOException e) { //if file is not found error is printed and false is returned
            e.printStackTrace();
            return false;
        }
    }

    //function to check image size by converting the input into bytes
    private static byte[] getBytes(InputStream inputStream) throws IOException{
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != 1){
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();

    }
}
