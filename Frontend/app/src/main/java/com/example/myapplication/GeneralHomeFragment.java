package com.example.myapplication;

import static android.widget.Toast.makeText;
import static com.example.myapplication.Authorization.generateAuthToken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GeneralHomeFragment extends Fragment {

    // Declare your views here
    private TextView welcomeMessage;
    private ImageButton buttonProfile;
    private SeekBar moodSeekBar;
    private EditText journalEntry;
    private Button submitEntries;
    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private int userAge;

    private int moodId = -1;
    private static final String APP_API_URL = "http://coms-3090-066.class.las.iastate.edu:8080/";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_general_home, container, false);

        // init views
        welcomeMessage = rootView.findViewById(R.id.welcomeMessage);
        buttonProfile = rootView.findViewById(R.id.buttonProfile);
        moodSeekBar = rootView.findViewById(R.id.moodSeekBar);
        journalEntry = rootView.findViewById(R.id.editTextJournal);
        submitEntries = rootView.findViewById(R.id.submit);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get the passed user info from previous page
        userEmail = getActivity().getIntent().getStringExtra("userEmail");
        userID = getActivity().getIntent().getIntExtra("userID", 0);
        userAge = getActivity().getIntent().getIntExtra("userAge", 0);
        userFirstName = getActivity().getIntent().getStringExtra("userFirstName");
        userLastName = getActivity().getIntent().getStringExtra("userLastName");

        welcomeMessage.setText("Welcome to Cymind");
        if (!(userID == 0)) {
            welcomeMessage.append(", " + userFirstName + "!");
        }

        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StudentProfileActivity.class);
            intent.putExtra( "userFirstName", userFirstName);
            intent.putExtra( "userLastName", userLastName);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("userID", userID);
            intent.putExtra("userAge", userAge);
            startActivity(intent);
        });

        // Set a listener to handle changes and enforce discrete positions
        moodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Called anytime progress bar changes
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when the user starts touching the seek bar, required method
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Called when done tracking, required method
            }
        });

        submitEntries.setOnClickListener(view1 -> sendMoodEntry());
   }

    private void sendMoodEntry() {
        if (userID == 0) {
            makeText(getActivity().getApplicationContext(), "Please log in to submit mood and journal entries.", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject requestBody;
        try {
            requestBody = new JSONObject();
            // ALWAYS add user ID to request body
            requestBody.put("userId", userID);
            requestBody.put("moodRating", moodSeekBar.getProgress());
            // requestBody.put("journalId", unknown); OPTIONAL, ASSUMED NULL IF DISCLUDED
        } catch (JSONException e) {
            Log.e("JSONError", "Failed to create JSON request body", e);
            makeText(getActivity().getApplicationContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, // HTTP method
                APP_API_URL + "entries/mood",
                requestBody, // Request body (null for GET request)
                response -> {
                    try {
                        moodId = response.getInt("id");
                        Log.d("Volley Response", response.toString());
                        makeText(getActivity().getApplicationContext(), "Entries updated successfully", Toast.LENGTH_SHORT).show();

                        submitJournalEntry();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    // Log response for debugging

                },
                error -> {
                    // Log error details
                    Log.e("Volley Error", error.toString());

                    // Display an error message
                    makeText(getActivity().getApplicationContext(), "Entries failed to update. Please try again.", Toast.LENGTH_LONG).show();
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
                // Example parameter
                // params.put("param1", "value1");
                return new HashMap<>();
            }
        };

        // Adding request to the Volley request queue
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjReq);
    }


    private void submitJournalEntry() {
        JSONObject requestBody;
        if (journalEntry.getText().toString().isEmpty()) {
            Intent intent = new Intent(getActivity(), MoodActivity.class);
            intent.putExtra( "userFirstName", userFirstName);
            intent.putExtra( "userLastName", userLastName);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("userID", userID);
            intent.putExtra("userAge", userAge);
            intent.putExtra("moodEntry", moodSeekBar.getProgress());
            intent.putExtra("moodId", moodId);
            startActivity(intent);
            return;
        }
        try {
            requestBody = new JSONObject();
            // ALWAYS add user ID to request body
            requestBody.put("entryName", "Journal Entry");
            requestBody.put("content", journalEntry.getText().toString());
            requestBody.put("moodId", moodId != -1 ? moodId : JSONObject.NULL);
            // requestBody.put("journalId", unknown); OPTIONAL, ASSUMED NULL IF DISCLUDED
            // Send new mood entry (Integer)
        } catch (JSONException e) {
            Log.e("JSONError", "Failed to create JSON request body", e);
            makeText(getActivity().getApplicationContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, // HTTP method
                APP_API_URL + "entries/journal",
                requestBody, // Request body (null for GET request)
                response -> {
                    // Log response for debugging
                    Log.d("Volley Response", response.toString());
                    makeText(getActivity().getApplicationContext(), "Entries updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MoodActivity.class);
                    intent.putExtra( "userFirstName", userFirstName);
                    intent.putExtra( "userLastName", userLastName);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("userID", userID);
                    intent.putExtra("userAge", userAge);
                    intent.putExtra("moodEntry", moodSeekBar.getProgress());
                    intent.putExtra("moodId", moodId);
                    intent.putExtra("journalEntry", journalEntry.getText().toString());
                    startActivity(intent);
                },
                error -> {
                    // Log error details
                    Log.e("Volley Error", error.toString());
                    // Display an error message
                    makeText(getActivity().getApplicationContext(), "Entries failed to update. Please try again.", Toast.LENGTH_LONG).show();


                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
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
                // Example parameter
                // params.put("param1", "value1");
                return new HashMap<>();
            }
        };

        // Adding request to the Volley request queue
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
}