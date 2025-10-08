package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
public class MoodActivity extends AppCompatActivity {

    // TODO: DECLARE PRIVATE VARIABLES

    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private int userAge;
    private int newMoodEntry;
    private Button buttonReturn;

    // TODO: BUILD ONCREATE()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood); // Make sure this layout exists and is correct

        // TODO: ASSIGN PRIVATE VARIABLES

        //get the passed user info from previous page
        userEmail = getIntent().getStringExtra("userEmail");
        userID = getIntent().getIntExtra("userID", 0);
        userAge = getIntent().getIntExtra("userAge", 0);
        userFirstName = getIntent().getStringExtra("userFirstName");
        userLastName = getIntent().getStringExtra("userLastName");
        newMoodEntry = getIntent().getIntExtra("mood", 3);

        // XML Elements
        buttonReturn = findViewById(R.id.returnButton);

        // If user is a guest, send them back to the homepage
        if (userID == 0) {
            Toast.makeText(this, "You are not signed in. Please log in to record your mood.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MoodActivity.this, HomepageActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
            return;
        }

        // TODO: OPERATIONAL CODE
        //      TODO: Send newMoodEntry to backend
        //      TODO: Query recent mood history
        //      TODO: Display recent mood history

        // Return to homepage
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoodActivity.this, HomepageActivity.class);
                intent.putExtra( "userFirstName", userFirstName);
                intent.putExtra( "userLastName", userLastName);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("userID", userID);
                intent.putExtra("userAge", userAge);
                startActivity(intent);
            }
        });
    }

    // TODO: ADDITIONAL METHODS IF NEEDED
}
