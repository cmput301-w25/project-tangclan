package com.example.tangclan;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

public class SetupBirthdayActivity extends AppCompatActivity
{
    private DatePickerDialog datePickerDialog;
    private ImageButton dateButton;
    private TextView dateText;

    private Button SetupBirthdayToSetupPFPButton;
    int dayGlobal,monthGlobal,yearGlobal;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup_birthday);
        initDatePicker();
        dateButton = findViewById(R.id.date_icon);
        dateText= findViewById(R.id.date_text);
        SetupBirthdayToSetupPFPButton=findViewById(R.id.button_veri);

        dateText.setText(getTodaysDate());

        //TODO:
        //Hookup to database if needed

        SetupBirthdayToSetupPFPButton.setOnClickListener(view -> {
            if (CalculateAge(yearGlobal,monthGlobal,dayGlobal)<18){
                Log.d("TAgAGE","DisplayAge:"+CalculateAge(yearGlobal,monthGlobal,dayGlobal));
            }else{
                Intent intent = new Intent(SetupBirthdayActivity.this, ProfileActivity.class);//TODO: CHANGE PROFILEACTIVITY.JAVA TO SETUP profile pic activity!!!
                startActivity(intent);
            }

        });

    }

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dayGlobal=day;
                yearGlobal=year;
                monthGlobal=month;
                dateText.setText(date);

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public int CalculateAge(int year, int month, int dayOfMonth) {
        return Period.between(
                LocalDate.of(year, month, dayOfMonth),
                LocalDate.now()
        ).getYears();
    }


    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
}