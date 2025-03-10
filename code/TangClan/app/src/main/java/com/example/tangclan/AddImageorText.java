package com.example.tangclan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddImageorText extends Fragment {

    private ImageView imageView;
    private ImageHelper imageHelper;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_upload_picture, container, false);
        WizActivity activity = (WizActivity) requireActivity();

        imageView = view.findViewById(R.id.imageView);
        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        Button buttonLoadImage = view.findViewById(R.id.btnUploadImage); // New button

        imageHelper = new ImageHelper(this, cameraLauncher, galleryLauncher);

        buttonSelectImage.setOnClickListener(v -> imageHelper.showImagePickerDialog());

        buttonLoadImage.setOnClickListener(v -> imageHelper.retrieveBase64ImageFromFirebase(imageView));

        ImageView closeIcon = view.findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(v -> requireActivity().finish());
}
