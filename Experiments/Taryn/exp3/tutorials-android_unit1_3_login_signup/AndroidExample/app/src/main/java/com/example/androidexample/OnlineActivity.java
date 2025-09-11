package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class OnlineActivity extends AppCompatActivity {

    private EditText firstEditText;  // define username edittext variable
    private EditText lastEditText;  // define password edittext variable

    private Spinner dateSpinner;
    private Spinner timeSpinner;
    private Spinner profSpinner;
    private Button onlineButton;         // define login button variable
    private Button inPersonButton;        // define signup button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);            // link to Login activity XML

        /* initialize UI elements */
        firstEditText = findViewById(R.id.online_first_edt);
        lastEditText = findViewById(R.id.online_last_edt);
        dateSpinner = findViewById(R.id.online_date_spinner);
        timeSpinner = findViewById(R.id.online_time_spinner);
        profSpinner = findViewById(R.id.online_prof_spinner);
        onlineButton = findViewById(R.id.online_submit_btn);    // link to login button in the Login activity XML
        inPersonButton = findViewById(R.id.online_inPerson_btn);  // link to signup button in the Login activity XML

        String[] date = {"September 11", "September 12", "September 13", "September 14", "September 15"};
        String[] time = {"9:00", "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "1:00"};
        String[] prof = {"Dr.Benson", "Dr.Murphy", "Dr.Schulz", "Dr.Dunn", "Dr.Wadle"};

        ArrayAdapter<String> adapterDate;
        adapterDate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, date);
        adapterDate.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        dateSpinner.setAdapter(adapterDate);


        ArrayAdapter<String> adapterTime;
        adapterTime = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, time);
        adapterTime.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        timeSpinner.setAdapter(adapterTime);


        ArrayAdapter<String> adapterProf;
        adapterProf = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prof);
        adapterProf.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        profSpinner.setAdapter(adapterProf);

        /* click listener on login button pressed */
        onlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String first = firstEditText.getText().toString();
                String last = lastEditText.getText().toString();
                String userDate = dateSpinner.getSelectedItem().toString();
                String userTime = timeSpinner.getSelectedItem().toString();
                String userProf = profSpinner.getSelectedItem().toString();

                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(OnlineActivity.this, MainActivity.class);
                intent.putExtra("FIRST", first);  // key-value to pass to the MainActivity
                intent.putExtra("LAST", last);  // key-value to pass to the MainActivity
                intent.putExtra("DATE", userDate);
                intent.putExtra("TIME", userTime);
                intent.putExtra("PROF", userProf);
                startActivity(intent);  // go to MainActivity with the key-value data
            }
        });

        /* click listener on signup button pressed */
        inPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(OnlineActivity.this, InPersonActivity.class);
                startActivity(intent);  // go to SignupActivity
            }
        });
    }
}