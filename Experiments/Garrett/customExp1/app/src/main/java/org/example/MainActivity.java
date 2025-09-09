package org.example;

import static com.google.android.material.color.utilities.MaterialDynamicColors.background;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private Button happyBtn;
    private Button sadBtn;
    private Button neutralBtn;
    private TextView currentEmotion;
    private Switch darkModeSwitch;
    private LinearLayoutCompat mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // links to main activity xml file

        // initialize UI elements
        welcomeMessage = findViewById(R.id.main_welcome_msg);
        happyBtn = findViewById(R.id.main_happy_btn);
        sadBtn = findViewById(R.id.main_sad_btn);
        neutralBtn = findViewById(R.id.main_neutral_btn);
        currentEmotion = findViewById(R.id.main_current_emotion);
        darkModeSwitch = findViewById(R.id.darkmode_switch);
        mainLayout = findViewById(R.id.background);


        /* click listener on happy button pressed */
        happyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, update currentEmotion to happy */
                currentEmotion.setText("Happy!");
            }
        });
        /* click listener on neutral button pressed */
        neutralBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, update currentEmotion to neutral */
                currentEmotion.setText("Meh");
            }
        });
        /* click listener on sad button pressed */
        sadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, update currentEmotion to sad */
                currentEmotion.setText("Sad");
            }
        });

        /* Light-Dark Mode Switch */
        darkModeSwitch.setTextOn("Light Mode");
        darkModeSwitch.setTextOff("Dark Mode");
        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (darkModeSwitch.isChecked()) {
                    mainLayout.setBackgroundResource(R.color.black);
                } else {
                    mainLayout.setBackgroundResource(R.color.white);
                }
            }
        });
    }
}
