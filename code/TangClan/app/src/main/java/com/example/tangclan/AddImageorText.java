package com.example.tangclan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

public class AddImageorText extends Fragment {

    private ImageView imageView;
    private ImageHelper imageHelper;
    private WizActivity WizActivity;
    private ImageValidator validor;

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
        Button buttonLoadImage = view.findViewById(R.id.btnUploadImage);
        Button buttonSaveText = view.findViewById(R.id.buttonSaveText);
        TextView charCount = view.findViewById(R.id.charCount);
        TextInputLayout text = view.findViewById(R.id.text203);

        imageHelper = new ImageHelper(WizActivity);

        buttonSelectImage.setOnClickListener(v -> imageHelper.showImagePickerDialog());
        buttonLoadImage.setOnClickListener(v -> imageHelper.uriToBitmap(imageHelper.getImageUri()));

        buttonSaveText.setOnClickListener();

        text.set

        ImageView closeIcon = view.findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(v -> requireActivity().finish());

        return view;
} }
