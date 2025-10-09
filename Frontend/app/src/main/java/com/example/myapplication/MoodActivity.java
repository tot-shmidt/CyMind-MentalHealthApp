package com.example.myapplication;

import static android.widget.Toast.makeText;

import static com.example.myapplication.Authorization.generateAuthToken;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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
    private RadioButton updateMoodSubmitButton;
    private EditText updateMoodIdEditText;
    private EditText updateMoodRatingEditText;

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
        updateMoodSubmitButton = findViewById(R.id.updateMoodSubmitButton);
        updateMoodIdEditText = findViewById(R.id.updateMoodIdEditText);
        updateMoodRatingEditText = findViewById(R.id.updateMoodRatingEditText);

        // If user is a guest, send them back to the homepage
        if (userID == 0) {
            Toast.makeText(this, "You are not signed in. Please log in to record your mood.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MoodActivity.this, HomepageActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
            return;
        }

        // Queries recent mood entries and displays them to the screen
        getMoodEntries();

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

        updateMoodSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check value of updateMoodRatingEditText, 0: delete mood entry, 1: update mood entry
                if (updateMoodRatingEditText.getText().toString().equals("0")) {
                    deleteMoodEntry(Integer.parseInt(updateMoodIdEditText.getText().toString()));
                } else {
                    updateMoodEntry(Integer.parseInt(updateMoodIdEditText.getText().toString()), Integer.parseInt(updateMoodRatingEditText.getText().toString()));
                }
                updateMoodSubmitButton.setChecked(false);
                updateMoodIdEditText.setText("");
                updateMoodRatingEditText.setText("");
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
                            displayText.append("ID: ").append(m.getId()).append(" -- Date: ").append(m.getDate().substring(5)).append(" -- Mood: ").append(m.getMoodRating()).append("/5\n");
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

    private void deleteMoodEntry(int moodId) {
        //Creates new request defined as a DELETE request
        //Use StringRequest since there is no json response body, just status code
        String deleteURL = APP_API_URL + "entries/mood/" + moodId;
        StringRequest delete = new StringRequest(Request.Method.DELETE, deleteURL,
            response -> {
                //Display message saying user was deleted by identifying their id
                Toast.makeText(MoodActivity.this, "Mood entry with an id: " + userID + " was successfully deleted", Toast.LENGTH_LONG).show();
                getMoodEntries();
            },
            error -> {
                //display error message if one occurs
                Toast.makeText(MoodActivity.this, "Error deleting mood entry", Toast.LENGTH_LONG).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + Authorization.generateAuthToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        //finally, if no issues, add the deleted user to queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(delete);
    }

    private void updateMoodEntry(int moodId, int newRating) {
        if (newRating < 0 || newRating > 5) {
            makeText(getApplicationContext(), "Mood rating must be between 0 and 5", Toast.LENGTH_SHORT).show();
            return;
        }
        // For ratings from 1-5
        JSONObject requestBody;
        try {
            requestBody = new JSONObject();
            // ALWAYS add user ID to request body
            requestBody.put("userId", userID);
            requestBody.put("moodRating", newRating);
        } catch (JSONException e) {
            Log.e("JSONError", "Failed to create JSON request body", e);
            makeText(getApplicationContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT, // HTTP method
                APP_API_URL + "entries/mood/" + moodId, // API URL + userID
                requestBody, // Request body (null for GET request)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log response for debugging
                        Log.d("Volley Response", response.toString());
                        makeText(getApplicationContext(), "Mood entry updated successfully", Toast.LENGTH_SHORT).show();
                        getMoodEntries();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log error details
                        Log.e("Volley Error", error.toString());

                        // Display an error message
                        makeText(getApplicationContext(), "Mood entry update failed. Please try again.", Toast.LENGTH_LONG).show();
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
