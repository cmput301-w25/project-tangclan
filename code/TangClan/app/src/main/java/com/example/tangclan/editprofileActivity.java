package com.example.tangclan;

import static android.text.TextUtils.isEmpty;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Activity that facilitates the edit profile information process
 * RELATED USER STORIES: US 03.01.01
 *      03.01.01c
 */
public class editprofileActivity extends AppCompatActivity {

    TextView usernameText;

    ArrayList<Profile> ProfileArrayList;
    EditText EditTextInputName,EditTextinputEmail,EditTextinputUsername,EditTextinputPassword, EditTextinputConfirmPassword ;

    Button submitChangesButton;

    private Profile userProfile;

    private DatabaseBestie databaseBestie;


    //Profile profile= new Profile("Person1", "aBcd123*","shaian@ualberta.ca","21");
    //Profile profile2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editprofile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initializing database
        databaseBestie = new DatabaseBestie();


        userProfile = LoggedInUser.getInstance();
        EditTextInputName=findViewById(R.id.edit_name_input);
        EditTextinputEmail=findViewById(R.id.edit_email_input);
        EditTextinputUsername=findViewById(R.id.new_username_input);
        EditTextinputPassword= findViewById(R.id.new_password_input);
        EditTextinputConfirmPassword=findViewById(R.id.confirm_new_input);
        //Bundle extras = getIntent().getExtras();

        //assert extras != null;
        //Profile profile2= (Profile) extras.get("Key1");



        usernameText=findViewById(R.id.username);
        assert userProfile != null;
        usernameText.setText(userProfile.getUsername());




        submitChangesButton = findViewById(R.id.submit_changes_button);
        submitChangesButton.setOnClickListener(view -> {


            if(ChangeProfileAndCheckErrors(userProfile)) {
                //Note: changeprofileandcheckerrors method makes changes to the profile class if all conditions pass
                Intent intent = new Intent(editprofileActivity.this, ProfilePageActivity.class);
                //intent.putExtra("Key1", profile2);
                //saveProfileToDatabase();//Note commented out for now, put the method inside of changeprofileandcheckerrors
                startActivity(intent);
            }
        });


    }





    public static boolean validPassword(String password){
        final String SPECIAL_CHARACTERS = "!,#,$,%,^,&,*,|,?";
        boolean upCase = false;
        boolean loCase = false;
        boolean isDigit = false;
        boolean spChar = false;
        boolean isLength=false;
        if (password.matches(".*[A-Z].*")){
            upCase = true;
        }
        if (password.matches(".*[a-z].*")){
            loCase = true;
        }
        if (password.matches(".*[0-9].*")){
            isDigit = true;
        }
        if (password.matches(".*[!#$%^&*|?].*")){
            spChar = true;
        }
        if((password.length()>=8)){
            isLength=true;
        }

        return (upCase && loCase && isDigit && spChar && isLength);
    }


    private boolean validUsername(String username){
        if (username.length()<=20 && username.length()>=0){
            return true;
        }
        return false;
    }


    private boolean ChangeProfileAndCheckErrors(Profile profile2){
        String StringName= EditTextInputName.getText().toString();
        String StringEmail= EditTextinputEmail.getText().toString();
        String StringUsername= EditTextinputUsername.getText().toString();
        String StringPassword= EditTextinputPassword.getText().toString();
        String StringConfirmPassword= EditTextinputConfirmPassword.getText().toString();

        if (StringName.isEmpty()){
            EditTextInputName.setError("A name is required");
            EditTextInputName.requestFocus();
            return false;
        }



        if (StringEmail.isEmpty() && StringEmail.equals(profile2.getEmail())){
            EditTextinputEmail.setError("Email required and must be the same when you first logged in");
            EditTextinputEmail.requestFocus();
            return false;
        }

        if (StringUsername.isEmpty() || !validUsername(StringUsername) ){//Note: MUST CHECK FOR UNIQUE USERNAME TOO
            EditTextinputUsername.setError("Username required");
            EditTextinputUsername.requestFocus();
            return false;
        }

        if (!validPassword(StringPassword)){
            EditTextinputPassword.setError("Password required or Password must contain all of the following:lower and upper case letter, a special character, a number and greater than a length of 8");
            EditTextinputPassword.requestFocus();
            return false;
        }


        if (!StringPassword.equals(StringConfirmPassword)){
            EditTextinputConfirmPassword.setError("Confirm password does not match!");
            EditTextinputConfirmPassword.requestFocus();
            return false;
        }

        //Setting values for profile object
        profile2.setEmail(StringEmail);
        profile2.setUsername(StringUsername);
        profile2.setPassword(StringPassword);
        profile2.setDisplayName(StringName);
        SaveToDatabase(databaseBestie,profile2);


        return true;

    }
    public void SaveToDatabase(DatabaseBestie db, Profile profile) {
        db.updateUser(profile.getUid(),profile);

    }
}