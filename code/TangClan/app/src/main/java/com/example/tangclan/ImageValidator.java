package com.example.tangclan;

import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils.getPath;
import static com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EncodingUtils.getBytes;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ImageValidator extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_setup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_picture), (v, insets)->{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

                ActivityCompat.requestPermissions(this, new String[]{READ_MEDIA_IMAGES}, PackageManager.PERMISSION_GRANTED);
            }
            return insets;
        });

        }

        public void buttonImageToBytes(View view){
            StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
            StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                File fileImage = new File(storageVolume.getDirectory().getPath() + "/Download/images.jpg");
                try{
                    byte[] bytes = Files.readAllBytes(fileImage.toPath());
                    textView.setText(bytes.length);
                } catch (IOException e){
                    throw new RuntimeException(e);

                }

            }


        }
    }







