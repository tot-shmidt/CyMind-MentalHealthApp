package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextView firstText;
    private TextView lastText;
    private TextView timeText;
    private TextView dateText;
    private TextView locText;
    private TextView profText;
    // define username textview variable
    private Button onlineButton;     // define login button variable
    private Button inPersonButton;    // define signup button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        firstText = findViewById(R.id.main_first_txt);// link to username textview in the Main activity XML
        lastText = findViewById(R.id.main_last_txt);
        timeText = findViewById(R.id.main_time_txt);
        dateText = findViewById(R.id.main_date_txt);
        profText = findViewById(R.id.main_prof_txt);
        locText = findViewById(R.id.main_loc_txt);
        onlineButton = findViewById(R.id.main_online_btn);    // link to login button in the Main activity XML
        inPersonButton = findViewById(R.id.main_inPerson_btn);  // link to signup button in the Main activity XML

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            messageText.setText("Welcome! To make an appointment, select one of the options below.");
            firstText.setVisibility(View.INVISIBLE);             // set username text invisible initially
        } else {
            messageText.setText("Your appointment is booked!");
            // this will come from LoginActivity
            firstText.setText(extras.getString("FIRST"));
            //lastText.setText(extras.getString("LAST"));
            dateText.setText(extras.getString("DATE"));
            timeText.setText(extras.getString("TIME"));
            profText.setText(extras.getString("PROF"));
            locText.setText(extras.getString("LOC"));


            onlineButton.setVisibility(View.INVISIBLE);              // set login button invisible
            inPersonButton.setVisibility(View.INVISIBLE);             // set signup button invisible
        }

        /* click listener on login button pressed */
        onlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
                startActivity(intent);
            }
        });

        /* click listener on signup button pressed */
        inPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(MainActivity.this, InPersonActivity.class);
                startActivity(intent);
            }
        });
    }
}