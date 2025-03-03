package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    //TODO: get the connection to database
    // we can use the username entered to match users in the database
    // we provide uid as information to the next activity (main activity) to act as the session user
    // upon successful authentication, onLoginSuccess() is called and the next activity begins

    /*
    set initially to -1 to allow
    ideally you want to check if the username matches some username in the database
    then once authentication succeeds we may change the authUID to the user ID
    then we can pass it as information to the next activity
     */
    private Integer authUID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.page_login);
    }

    public void onLoginSuccess() {
        // pass a context bc the next activity needs to know the state of the program
        Intent intent= new Intent(this, MainActivity.class);
        //TODO: check first if the authID > 0. if not then raise some exception
        intent.putExtra("user-id", this.authUID);
        startActivity(intent);
    }
}