package org.example;

import static com.google.android.material.color.utilities.MaterialDynamicColors.background;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.w3c.dom.Text;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private TextView apptInstructions;
    private TextView apptConfirmation;
    private Button schedule;
    private DatePicker apptDate;
    private TimePicker apptTime;
    private String date;
    private String time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // links to main activity xml file

        // initialize UI elements
        welcomeMessage = findViewById(R.id.main_welcome_msg);
        apptInstructions = findViewById(R.id.appt_instructions);
        apptConfirmation = findViewById(R.id.appt_confirmation);
        schedule = findViewById(R.id.schedule_btn);
        apptDate = findViewById(R.id.datePicker);
        apptTime = findViewById(R.id.timePicker);

        Calendar today = Calendar.getInstance();

        apptDate.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                date = (month + 1) + "/" + day + "/" + year;
            }});

        apptTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                int hour = hourOfDay;
                String amPm;

                // Determine AM or PM and adjust hour
                if (hour == 0) {
                    hour += 12;
                    amPm = "AM";
                } else if (hour == 12) {
                    amPm = "PM";
                } else if (hour > 12) {
                    hour -= 12;
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }

                // Format hour and minute for display
                String formattedHour = (hour < 10) ? "0" + hour : String.valueOf(hour);
                String formattedMinute = (minute < 10) ? "0" + minute : String.valueOf(minute);

                // Display the selected time
                time = formattedHour + " : " + formattedMinute + " " + amPm;
            }
        });


        // Schedule Button
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when btn is pressed, take date and time and put into confirmation message
                apptConfirmation.setText("Appointment scheduled for " + date + " at " + time);
            }
        });

    }
}
