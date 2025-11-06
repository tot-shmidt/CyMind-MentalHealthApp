package com.example.myapplication;

import static android.widget.Toast.makeText;

import static com.example.myapplication.Authorization.generateAuthToken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ProfessionalProfileActivity extends AppCompatActivity {

    private static final String APP_API_URL = "http://coms-3090-066.class.las.iastate.edu:8080/users/";
    private TextView nameText;
    private TextView emailText;
    private TextView ageText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText ageEditText;
    private EditText passwordEditText;
    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userJobTitle;
    private String userLicenseNumber;
    private int userAge;
    private TextView jobTitleText;
    private TextView licenseNumberText;
    private EditText jobTitleEditText;
    private EditText licenseNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_profile);

        Button buttonReturn = findViewById(R.id.returnButton);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        ageText = findViewById(R.id.ageText);
        jobTitleText = findViewById(R.id.jobTitleText);
        licenseNumberText = findViewById(R.id.licenseNumberText);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        ageEditText = findViewById(R.id.ageEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        jobTitleEditText = findViewById(R.id.jobTitleEditText);
        licenseNumberEditText = findViewById(R.id.licenseNumberEditText);

        Button updateProfileButton = findViewById(R.id.updateProfileButton);
        Button deleteProfilebutton = findViewById(R.id.deleteProfileButton);

        //get the passed email and id from previous pages
        userEmail = getIntent().getStringExtra("userEmail");
        userID = getIntent().getIntExtra("userID", 0);
        userAge = getIntent().getIntExtra("userAge", 0);
        userFirstName = getIntent().getStringExtra("userFirstName");
        userLastName = getIntent().getStringExtra("userLastName");
        userJobTitle = getIntent().getStringExtra("userJobTitle");
        userLicenseNumber = getIntent().getStringExtra("userLicenseNumber");




        //if a userID was not created successfully, display error and send back to homepage
        if (userID == 0) {
            Toast.makeText(this, "You are not signed in. Please log in to view your profile", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ProfessionalProfileActivity.this, ProfessionalFragmentActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
            return;
        } else {
            // Set user data to display
            nameText.setText("Name: " + userFirstName + " " + userLastName);
            emailText.setText("Email: " + userEmail);
            ageText.setText("Age: " + userAge);
            jobTitleText.setText("Job Title: " + (userJobTitle != null ? userJobTitle : "N/A"));
            licenseNumberText.setText("License #: " + (userLicenseNumber != null ? userLicenseNumber : "N/A"));
        }

        // Method for deleting the user and sending request to postman
        deleteProfilebutton.setOnClickListener(view -> {

            Authorization.globalUserEmail = userEmail;
            Authorization.globalPassword = passwordEditText.getText().toString();


            //Creates new DELETE request
            //Use StringRequest since there is no json response body, just status code
            String deleteURL = APP_API_URL + userID;
            StringRequest delete = new StringRequest(Request.Method.DELETE, deleteURL, response -> {

                //Display message saying user was deleted by identifying their id
                Toast.makeText(ProfessionalProfileActivity.this, "User with an id: " + userID + " was successfully deleted", Toast.LENGTH_LONG).show();
                //Upon user deletion, go back to the sign up page to create new user
                Intent intent = new Intent(ProfessionalProfileActivity.this, WelcomeActivity.class);
                startActivity(intent);
                },
                    error -> {
                //display error message if one occurs
                Toast.makeText(ProfessionalProfileActivity.this, "Error deleting user", Toast.LENGTH_LONG).show();
                }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Basic " + Authorization.generateAuthToken());
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

            };

            //finally, if no issues, add the deleted user to queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(delete);
        });

        buttonReturn.setOnClickListener(view -> {
            Intent intent = new Intent(ProfessionalProfileActivity.this, ProfessionalFragmentActivity.class);
            intent.putExtra( "userFirstName", userFirstName);
            intent.putExtra( "userLastName", userLastName);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("userID", userID);
            intent.putExtra("userAge", userAge);
            intent.putExtra("userJobTitle", userJobTitle);
            intent.putExtra("userLicenseNumber", userLicenseNumber);
            startActivity(intent);
        });

        updateProfileButton.setOnClickListener(view -> userUpdate());
    }
    private void userUpdate() {
        JSONObject requestBody;
        try {
            requestBody = new JSONObject();
            // ALWAYS add user ID to request body
            requestBody.put("id", userID);
            // Add email if updated, otherwise keep current
            if (!emailEditText.getText().toString().isEmpty()) {
                userEmail = emailEditText.getText().toString().trim();
            }
            requestBody.put("email", userEmail);
            // Split name, add if updated, otherwise keep current
            if (!nameEditText.getText().toString().isEmpty()) {
                String[] nameParts = nameEditText.getText().toString().trim().split(" ");
                if (nameParts.length == 1) {
                    userFirstName = nameParts[0];
                    userLastName = "";
                } else {
                    userFirstName = "";
                    for (int i = 0; i < nameParts.length - 1; i++) {
                        userFirstName += nameParts[i] + " ";
                    }
                    userFirstName = userFirstName.trim();
                    userLastName = nameParts[nameParts.length-1];
                }
            }
            requestBody.put("firstName", userFirstName);
            requestBody.put("lastName", userLastName);
            if (!ageEditText.getText().toString().isEmpty()) {
                userAge = Integer.parseInt(ageEditText.getText().toString());
            }
            requestBody.put("age", userAge);
            // Update job title if changed
            if (!jobTitleEditText.getText().toString().isEmpty()) {
                userJobTitle = jobTitleEditText.getText().toString().trim();
            }
            // Update license number if changed
            if (!licenseNumberEditText.getText().toString().isEmpty()) {
                userLicenseNumber = licenseNumberEditText.getText().toString().trim();
            }
            if (passwordEditText.getText().toString().isEmpty()) {
                makeText(getApplicationContext(), "Enter your password to update information", Toast.LENGTH_LONG).show();
            }
            requestBody.put("password", passwordEditText.getText().toString());
        } catch (JSONException e) {
            Log.e("JSONError", "Failed to create JSON request body", e);
            makeText(getApplicationContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
            Request.Method.PUT, // HTTP method
            APP_API_URL + userID, // API URL + userID
            requestBody, // Request body (null for GET request)
                response -> {
                    // Log response for debugging
                    Log.d("Volley Response", response.toString());

                    // Update professional-specific info
                    updateProfessionalInfo();

                    makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    // Update on screen text
                    nameText.setText("Name: " + userFirstName + " " + userLastName);
                    emailText.setText("Email: " + userEmail);
                    ageText.setText("Age: " + userAge);
                    jobTitleText.setText("Job Title: " + (userJobTitle != null ? userJobTitle : "N/A"));
                    licenseNumberText.setText("License #: " + (userLicenseNumber != null ? userLicenseNumber : "N/A"));
                },
                error -> {
                    // Log error details
                    Log.e("Volley Error", error.toString());

                    // Display an error message
                    makeText(getApplicationContext(), "Profile update failed. Please try again.", Toast.LENGTH_LONG).show();
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    private void updateProfessionalInfo() {
        JSONObject requestProfessional = new JSONObject();
        try {
            requestProfessional.put("jobTitle", userJobTitle);
            requestProfessional.put("licenseNumber", userLicenseNumber);
            requestProfessional.put("userId", userID);
        } catch (JSONException e) {
            Log.e("JSONError", "Failed to create professional info request", e);
            return;
        }

        String professionalURL = "http://coms-3090-066.class.las.iastate.edu:8080/users/professional";

        JsonObjectRequest updateProfRequest = new JsonObjectRequest(
            Request.Method.PUT,
            professionalURL + "/" + userID,
            requestProfessional,
            response -> {
                Log.d("UpdateProfessionalInfo", "Professional info updated successfully");
            },
            error -> {
                Log.e("UpdateProfessionalInfo", "Error updating professional info: " + error.toString());
                Log.e("JSON output", requestProfessional.toString());
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(updateProfRequest);
    }
}
