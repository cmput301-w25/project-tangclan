package com.example.tangclan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Activity that facilitates the edit profile information process
 * RELATED USER STORIES: US 03.01.01
 *      03.01.01c
 */
public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private static final int CROP_IMAGE_REQUEST = 3;


    ImageView profilePic, cancel;
    TextView usernameText,displayNameText;
    EditText EditTextInputName,EditTextInputEmail,EditTextInputUsername,EditTextInputPassword, EditTextInputConfirmPassword ;
    Button submitChangesButton;
    private Profile userProfile;
    private DatabaseBestie databaseBestie;
    private ImageHelper imageHelper;
    private Uri imageUri = null;
    private Bitmap selectedImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editprofile_new);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initializing database
        databaseBestie = new DatabaseBestie();
        imageHelper = new ImageHelper(this,cameraLauncher,galleryLauncher);

        // get views
        userProfile = LoggedInUser.getInstance();
        EditTextInputName=findViewById(R.id.edit_name_input);
        EditTextInputEmail=findViewById(R.id.edit_email_input);
        EditTextInputUsername=findViewById(R.id.new_username_input);
        EditTextInputPassword= findViewById(R.id.new_password_input);
        EditTextInputConfirmPassword=findViewById(R.id.confirm_new_input);
        usernameText=findViewById(R.id.username);
        displayNameText=findViewById(R.id.nameDisplay);
        profilePic = findViewById(R.id.pfpView);
        cancel = findViewById(R.id.closeIcon);

        // set saved details
        if (userProfile != null) {
            if (userProfile.getProfilePic() != null) {
                profilePic.setImageBitmap(imageHelper.base64ToBitmap(userProfile.getProfilePic()));
            }
            usernameText.setText(userProfile.getUsername());
            displayNameText.setText(userProfile.getDisplayName() != null? userProfile.getDisplayName(): userProfile.getUsername());
            EditTextInputName.setText(userProfile.getDisplayName());
            EditTextInputEmail.setText(userProfile.getEmail());
            EditTextInputUsername.setText(userProfile.getUsername());
            EditTextInputPassword.setText(userProfile.getPassword());
            EditTextInputConfirmPassword.setText(userProfile.getPassword());
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSourceDialog();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, ProfilePageActivity.class);
                startActivity(intent);
                finish();
            }
        });


        submitChangesButton = findViewById(R.id.submit_changes_button);
        submitChangesButton.setOnClickListener(view -> {
            if (ChangeProfileAndCheckErrors(userProfile)) {
                Intent intent = new Intent(EditProfileActivity.this, ProfilePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button logout = findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(EditProfileActivity.this, LoginOrSignupActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void saveProfileToDatabase(Profile userProfile) {
        // This method should save the updated profile to your database
        // For now, we'll just simulate successful saving
        //databaseBestie.
    }



    public static boolean validPassword(String password){
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!#$%^&*|])[A-Za-z\\d!#$%^&*|]{8,}$");
    }


    private boolean validUsername(String username){
        if (!username.toLowerCase().matches("^[a-z0-9_-]{4,}$")){
            return false;
        }
        return true;
    }

    private boolean validEmail(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            return false;
        }
        boolean[] exists = new boolean[1];
        databaseBestie.checkEmailExists(email, ema -> {
            exists[0] = ema == null;
        });
        return exists[0];
    }


    private boolean ChangeProfileAndCheckErrors(Profile profile2){
        String stringName= EditTextInputName.getText().toString();
        // String stringEmail= EditTextInputEmail.getText().toString();
        String stringUsername= EditTextInputUsername.getText().toString();
        String stringPassword= EditTextInputPassword.getText().toString();
        String stringConfirmPassword= EditTextInputConfirmPassword.getText().toString();

        // should not be able to change email
        /*
        if (stringEmail.isEmpty()) {
            EditTextInputEmail.setError("Enter email");
            return false;
        }

        if (!validEmail(stringEmail) && !stringEmail.equals(profile2.getEmail())) {
            EditTextInputEmail.setError("Wrong format or already in use");
            return false;
        }
        if (!stringEmail.equals(profile2.getEmail())){
            EditTextInputEmail.setError("Email must be the same when you first logged in");
            EditTextInputEmail.requestFocus();
            return false;
        }
         */

        if (stringUsername.isEmpty()) {
            EditTextInputUsername.setError("Username required");
        }

        if (!validUsername(stringUsername) && !stringUsername.equals(profile2.getUsername())){//Note: MUST CHECK FOR UNIQUE USERNAME TOO
            EditTextInputUsername.setError("Username must at least 4 characters long and can only contain:\n" +
                    " - alphanumeric characters\n" +
                    " - hyphens\n" +
                    " - underscores\n");
            EditTextInputUsername.requestFocus();
            return false;
        }

        if (!validPassword(stringPassword)){
            EditTextInputPassword.setError("Password must be at least 8 characters long and must contain one of each:\n" +
                    " - Capital letter\n" +
                    " - Lowercase letter\n" +
                    " - One of the special characters: !,#,$,%,^,&,*,|\n");
            EditTextInputPassword.requestFocus();
            return false;
        }


        if (!stringPassword.equals(stringConfirmPassword)){
            EditTextInputConfirmPassword.setError("Confirm password does not match!");
            EditTextInputConfirmPassword.requestFocus();
            return false;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // Replace with the user's current email & password
            AuthCredential credential = EmailAuthProvider.getCredential(profile2.getEmail(), profile2.getPassword());

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    System.out.println("Re-authentication successful");
                    user.updatePassword(stringPassword);
                } else {
                    System.out.println("Re-authentication failed: " + task.getException().getMessage());
                }
            });
        }

        //Setting values for profile object
        if (selectedImage != null) {
            profile2.setProfilePic(imageHelper.bitmapToBase64(selectedImage));
        }
        // profile2.setEmail(stringEmail);

        databaseBestie.findEmailByUsername(stringUsername, em -> {
            if (em != null) {
                if (!stringUsername.equals(profile2.getUsername())) {
                    // username changed and is not taken
                    EditTextInputUsername.setError("Username already taken");
                }
            } else {
                profile2.setUsername(stringUsername);
                databaseBestie.updateUserUsername(profile2.getUid(),stringUsername);
            }
        });
        profile2.setPassword(stringPassword);
        profile2.setDisplayName(stringName);
        saveToDatabase(databaseBestie,profile2);

        return true;

    }
    public void saveToDatabase(DatabaseBestie db, Profile profile) {
        String id = profile.getUid();
        db.updateUserPhoto(id,profile.getProfilePic());
        // db.updateUserUsername(id, profile.getUsername());
        db.updateUserDisplayName(id, profile.getDisplayName());
        db.updateUserPassword(id, profile.getPassword());
    }

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageUri = imageHelper.getImageUri();
                    profilePic.setImageURI(imageUri);
                    selectedImage = imageHelper.uriToBitmap(imageUri);
                }
            });

    // Gallery launcher
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    profilePic.setImageURI(imageUri);
                    selectedImage = imageHelper.uriToBitmap(imageUri);
                }
            });

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                captureImageFromCamera();
            } else {
                //pickImageFromGallery();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(intent);
            }
        });
        builder.show();
    }

    private void captureImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null) {
                imageUri = data.getData();
                // Validate the image size before continuing
                if (ImageValidator.isImageSizeValid(this, imageUri)) {
                    startCropActivity(imageUri);
                }
            } else if (requestCode == CAMERA_REQUEST && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    imageUri = getImageUri(photo);
                    if (imageUri != null) {

                        if (ImageValidator.isImageSizeValid(this, imageUri)) {
                            startCropActivity(imageUri);
                        }
                    } else {
                        Toast.makeText(this, "Failed to process captured image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CROP_IMAGE_REQUEST) {
                handleCropResult(data);
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);  // Use PNG instead of JPEG

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CapturedImage", null);

        if (path == null) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }

        return Uri.parse(path);
    }

    private void startCropActivity(Uri sourceUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(sourceUri, "image/*");

        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 500);
        cropIntent.putExtra("outputY", 500);
        cropIntent.putExtra("return-data", true);
        cropIntent.putExtra("circleCrop", true);


        if (cropIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cropIntent, CROP_IMAGE_REQUEST);
        } else {
            Toast.makeText(this, "Cropping not supported, displaying original image", Toast.LENGTH_SHORT).show();
            displaySelectedImage(sourceUri);
        }
    }

    private void handleCropResult(Intent data) {
        if (data.getExtras() != null && data.getExtras().getParcelable("data") != null) {
            Bitmap croppedBitmap = data.getExtras().getParcelable("data");
            displaySelectedBitmap(croppedBitmap);
        } else if (data.getData() != null) {
            Uri croppedImageUri = data.getData();
            displaySelectedImage(croppedImageUri);
        } else {
            Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
            if (imageUri != null) {
                displaySelectedImage(imageUri);
            }
        }
    }

    private void displaySelectedImage(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= 28) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }

            runOnUiThread(() -> {
                profilePic.setImageBitmap(bitmap);
                Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void displaySelectedBitmap(Bitmap bitmap) {
        profilePic.setImageBitmap(bitmap);
        selectedImage = bitmap;
        Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
    }
}