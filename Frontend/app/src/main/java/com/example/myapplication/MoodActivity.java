package com.example.myapplication;

import static android.widget.Toast.makeText;

import static com.example.myapplication.Authorization.generateAuthToken;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MoodActivity extends AppCompatActivity {

    // TODO: DECLARE PRIVATE VARIABLES
    private static final String APP_API_URL = "http://coms-3090-066.class.las.iastate.edu:8080/";
    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private int userAge;
    private int newMoodEntry;
    private Button buttonReturn;
    private TextView moodDataText;

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
        moodDataText = findViewById(R.id.moodData);

        // If user is a guest, send them back to the homepage
        if (userID == 0) {
            Toast.makeText(this, "You are not signed in. Please log in to record your mood.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MoodActivity.this, HomepageActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
            return;
        }

        // TODO: OPERATIONAL CODE
        //      TODO: Query recent mood history
        getMoodEntries();
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

    private void getMoodEntries() {
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(
                Request.Method.GET, // HTTP method
                APP_API_URL + "entries/mood",
                null, // Request body (null for GET request)
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Log response for debugging
                        Log.d("Volley Response", response.toString());
                        List<MoodEntry> moodList = new ArrayList<>();
                        // Parse JSONArray by sending it to MoodEntry to become a JSONObject
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                MoodEntry entry = new MoodEntry(
                                        obj.getInt("id"),
                                        obj.getString("date"),
                                        obj.getInt("moodRating"),
                                        obj.getInt("userId"),
                                        obj.isNull("journalId") ? null : obj.getString("journalId")
                                );
                                moodList.add(entry);
                            }
                        } catch (JSONException e) {
                            Log.e("MoodEntry", "Failed to parse JSON response");
                        }

                        // Display mood entries
                        StringBuilder displayText = new StringBuilder();
                        for (MoodEntry m : moodList) {
                            displayText.append("Date: ").append(m.getDate()).append(" -- Mood: ").append(m.getMoodRating()).append("/5\n");
                        }
                        moodDataText.setText(displayText.toString());

                        makeText(getApplicationContext(), "Retrieved mood entries successfully.", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log error details
                        Log.e("Volley Error (getMoodEntries)", error.toString());

                        // Display an error message
                        makeText(getApplicationContext(), "Failed to retrieve mood entries. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Define headers if needed
                HashMap<String, String> headers = new HashMap<>();
                // Headers
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                // Define parameters if needed
                Map<String, String> params = new HashMap<>();
                // Example parameter
                // params.put("param1", "value1");
                return params;
            }
        };

        // Adding request to the Volley request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

}
