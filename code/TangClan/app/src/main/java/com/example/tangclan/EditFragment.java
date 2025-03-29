package com.example.tangclan;

import static android.view.View.FIND_VIEWS_WITH_TEXT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;

public class EditFragment extends Fragment {

    String[] emotions = {"happy",
                        "sad",
                        "angry",
                        "anxious",
                        "ashamed",
                        "calm",
                        "confused",
                        "disgusted",
                        "no idea",
                        "surprised",
                        "terrified"};
    String[] socialSituations = {"", "alone", "with one other person", "with two to several people", "with a crowd"};
    String mid, month, emotion, setting, situation, reason;
    byte[] image;
    boolean locationPermission;
    private FragmentListener editFragmentListener;
    private ImageView imageView;

    ImageHelper imageHelper;
    private ImageButton imageButton;
    private Uri imageUri = null;
    private Bitmap selectedImage = null;


    public interface FragmentListener {  // listens to when fragment finishes
        void onFragmentFinished();
    }

    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance(Bundle moodDetails) {
        EditFragment fragment = new EditFragment();
        fragment.setArguments(moodDetails);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            editFragmentListener = (FragmentListener) context; // Attach listener
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mid = getArguments().getString("mid");
            month = getArguments().getString("month");
            emotion = getArguments().getString("emotion");
            setting = getArguments().getString("setting");
            situation = getArguments().getString("social situation");
            reason = getArguments().getString("reason");
            image = getArguments().getByteArray("image");
            locationPermission = getArguments().getBoolean("location permission");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_details_fragment, container, false);

        AutoCompleteTextView autoCompleteEmotion = view.findViewById(R.id.choose_emotion);
        String[] newEmotion = new String[1];
        newEmotion[0] = emotion; // set current emotion
        setUpDropdown(autoCompleteEmotion, newEmotion, emotions);

        // set saved social situation
        String[] newSit = new String[1];
        newSit[0] = setting;
        AutoCompleteTextView autoCompleteSituation = view.findViewById(R.id.choose_social_situation);
        setUpDropdown(autoCompleteSituation, newSit, socialSituations);

        // set up collaborators
        EditText editSocialSit = view.findViewById(R.id.edit_social_situation);
        editSocialSit.setText(situation);

        // set saved reason text
        EditText editReason = view.findViewById(R.id.edit_reasonwhy);
        editReason.setText(reason);

        // TODO: on text change listener, update character count


        imageButton = view.findViewById(R.id.image_reasonwhy);
        // set saved image
        if (image != null) {
            imageButton.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

        imageHelper = new ImageHelper(requireActivity(), cameraLauncher, galleryLauncher);
        imageButton.setOnClickListener(v -> {
            imageHelper.showImagePickerDialog();
        });

        // set saved location permission
        SwitchCompat useLocation = view.findViewById(R.id.use_location_switch);
        useLocation.setChecked(locationPermission);

        // implement submit button
        Button submitButt = view.findViewById(R.id.submit_details);
        submitButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newReason = editReason.getText().toString();

                if (newReason.length() > 200) { // hi alissa
                    editReason.setError("Text is over 200 characters!");
                    return;
                }


                // String newEmotion = getCheckedEmotionText(view, radioButtons);
                ArrayList<String> newCollaborators = getEditTextCollaborators(editSocialSit);

                // validate image
                if (imageUri != null) {
                    // Using the static method from ImageValidator
                    if (ImageValidator.isImageSizeValid(getActivity(), imageUri)) {
                        selectedImage = imageHelper.uriToBitmap(imageUri);
                    } else {
                        Toast.makeText(getActivity(), "Image is too large!", Toast.LENGTH_SHORT).show();
                        return; // not valid
                    }
                } else {
                    // img not changed
                    if (image != null) {
                        selectedImage = imageHelper.base64ToBitmap(Base64.encodeToString(image, Base64.DEFAULT));  // select the same image
                    }
                }
                // save image as string
                String newImg;
                if (selectedImage != null) {
                    byte[] compressedImg = ImageValidator.compressBitmapToSize(selectedImage);
                    newImg = Base64.encodeToString(compressedImg, Base64.DEFAULT);
                } else {
                    newImg = null;
                }
                saveEditsToDatabase(newEmotion[0], newReason, newCollaborators, newImg);  // TODO: add setting and permisisions

                finishFragment();
            }
        });

        // Implement Cancel Button
        ImageButton cancel_butt = view.findViewById(R.id.cancel_edit);
        cancel_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishFragment();
            }
        });

        return view;
    }


    public void setUpDropdown(AutoCompleteTextView autoCompleteView, String[] saved, String[] items) {
        autoCompleteView.setText(saved[0]);
        autoCompleteView.setHint(saved[0]);
        ArrayAdapter<String> options = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.dropdown_item, items);
        autoCompleteView.setAdapter(options);

        autoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                saved[0] = adapterView.getItemAtPosition(i).toString();
                autoCompleteView.setHint(adapterView.getItemAtPosition(i).toString());
            }
        });
    }

    public ArrayList<String> getEditTextCollaborators(EditText inputLine) {
        String[] situationArray = inputLine.getText().toString().split(",");
        return new ArrayList<>(Arrays.asList(situationArray));
    }

    // Camera launcher
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // First try to get thumbnail from intent extras
                    if (data != null && data.getExtras() != null) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        if (bitmap != null) {
                            selectedImage = bitmap;
                            imageButton.setImageBitmap(selectedImage);
                            return;
                        }
                    }

                    // If no thumbnail, use the saved file URI
                    imageUri = imageHelper.getImageUri();
                    if (imageUri != null) {
                        try {
                            Bitmap bitmap = imageHelper.uriToBitmap(imageUri);
                            if (bitmap != null) {
                                selectedImage = bitmap;
                                imageButton.setImageBitmap(bitmap);
                            } else {
                                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    // Gallery launcher
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageButton.setImageURI(imageUri);
                    selectedImage = imageHelper.uriToBitmap(imageUri);
                }
            });

    public void saveEditsToDatabase(String emotion, String reason, ArrayList<String> socialSit, String image) {
        DatabaseBestie db = new DatabaseBestie();
        db.updateMoodEventEmotionalState(mid, month, emotion.toLowerCase());
        db.updateMoodEventReason(mid,month, reason);
        db.updateMoodEventCollaborators(mid, month, socialSit);
        db.updateMoodEventPhoto(mid,month,image);
    }

    private void finishFragment() {
        if (editFragmentListener != null) {
            editFragmentListener.onFragmentFinished(); // Notify Activity
        }
        getParentFragmentManager().beginTransaction().remove(this).commit(); // Remove Fragment
    }
}