package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class InPersonActivity extends AppCompatActivity {

    private EditText firstEditText;  // define username edittext variable
    private EditText lastEditText;  // define password edittext variable
    private Spinner dateSpinner;
    private Spinner timeSpinner;
    private Spinner locSpinner;
    private Spinner profSpinner;
    private EditText confirmEditText;   // define confirm edittext variable
    private Button onlineButton;         // define login button variable
    private Button inPersonButton;        // define signup button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inperson);

        /* initialize UI elements */
        firstEditText = findViewById(R.id.inPerson_first_edt);  // link to username edtext in the Signup activity XML
        lastEditText = findViewById(R.id.inPerson_last_edt);  // link to password edtext in the Signup activity XML
        dateSpinner = findViewById(R.id.inPerson_date_spinner);
        timeSpinner = findViewById(R.id.inPerson_time_spinner);
        locSpinner = findViewById(R.id.inPerson_loc_spinner);
        profSpinner = findViewById(R.id.inPerson_prof_spinner);
        onlineButton = findViewById(R.id.inPerson_online_btn);    // link to login button in the Signup activity XML
        inPersonButton = findViewById(R.id.inPerson_submit_btn);  // link to signup button in the Signup activity XML

        String[] date = {"September 11", "September 12", "September 13", "September 14", "September 15"};
        String[] time = {"9:00", "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "1:00"};
        String[] loc = {"120 Ash Ave", "453 Walnut St", "387 Knapp St", "400 Stanton Ave", "1738 Lincoln Way"};
        String[] prof = {"Dr.Benson", "Dr.Murphy", "Dr.Schulz", "Dr.Dunn", "Dr.Wadle"};

        ArrayAdapter<String> adapterDate;
        adapterDate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, date);
        adapterDate.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        dateSpinner.setAdapter(adapterDate);


        ArrayAdapter<String> adapterTime;
        adapterTime = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, time);
        adapterTime.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        timeSpinner.setAdapter(adapterTime);

        ArrayAdapter<String> adapterLoc;
        adapterLoc = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loc);
        adapterLoc.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        locSpinner.setAdapter(adapterLoc);



        ArrayAdapter<String> adapterProf;
        adapterProf = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prof);
        adapterProf.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        profSpinner.setAdapter(adapterProf);



        /* click listener on login button pressed */
        onlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(InPersonActivity.this, OnlineActivity.class);
                startActivity(intent);  // go to LoginActivity
            }
        });

        /* click listener on signup button pressed */
        inPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String first = firstEditText.getText().toString();
                String last = lastEditText.getText().toString();
                String userDate = dateSpinner.getSelectedItem().toString();
                String userTime = timeSpinner.getSelectedItem().toString();
                String userLoc = locSpinner.getSelectedItem().toString();
                String userProf = profSpinner.getSelectedItem().toString();

                Intent intent = new Intent(InPersonActivity.this, MainActivity.class);
                intent.putExtra("FIRST", first);  // key-value to pass to the MainActivity
                intent.putExtra("LAST", last);  // key-value to pass to the MainActivity
                intent.putExtra("DATE", userDate);
                intent.putExtra("TIME", userTime);
                intent.putExtra("LOC", userLoc);
                intent.putExtra("PROF", userProf);
                startActivity(intent);

            }
        });
    }
}