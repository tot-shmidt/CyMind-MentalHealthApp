package com.example.myapplication;

import static android.widget.Toast.makeText;

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
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String URL_JSON_OBJECT = "http://coms-3090-066.class.las.iastate.edu:8080/users/";
    private Button buttonReturn;
    private TextView nameText;
    private TextView emailText;
    private TextView ageText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText ageEditText;
    private EditText passwordEditText;
    private Button updateProfileButton;
    private Button deleteProfilebutton;
    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String originalUserEmail;
    private int userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Make sure this layout exists and is correct

        buttonReturn = findViewById(R.id.returnButton);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        ageText = findViewById(R.id.ageText);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        ageEditText = findViewById(R.id.ageEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        deleteProfilebutton = findViewById(R.id.deleteProfileButton);

        //get the passed email and id from previous pages
        userEmail = getIntent().getStringExtra("userEmail");
        originalUserEmail = userEmail;
        userID = getIntent().getIntExtra("userID", 0);
        userAge = getIntent().getIntExtra("userAge", 0);
        userFirstName = getIntent().getStringExtra("userFirstName");
        userLastName = getIntent().getStringExtra("userLastName");

        //if a userID was not created successfully, display error and send back to homepage
        if (userID == 0) {
            Toast.makeText(this, "You are not signed in. Please log in to view your profile", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
            return;
        } else {
            // Set user data to display
            nameText.setText("Name: " + userFirstName + " " + userLastName);
            emailText.setText("Email: " + userEmail);
            ageText.setText(String.valueOf("Age: " + userAge));
        }





        // Method for deleting the user (DELETE) and sending request to postman
        deleteProfilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //POSTMAN URL for our DELETE endpoint
                String deleteUserURL = "http://f5fb9954-c023-4687-984d-af55d0cd74f2.mock.pstmn.io/users/" + userID;
                //request conneection from this page

                //Tells postman this is a DELETE method for deleting a user
                JsonObjectRequest delete = new JsonObjectRequest(Request.Method.DELETE, deleteUserURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            //If the request was sucessful, display that on app so we know that the DELETE request worked and the user is deleted
                            public void onResponse(JSONObject response) {
                                Toast.makeText(ProfileActivity.this, "User was sucessfully deleted", Toast.LENGTH_LONG).show();
                                //Upon user deletion, go back to the sign up page to create new user
                                Intent intent = new Intent(ProfileActivity.this, SignUpActivity.class);
                                startActivity(intent);
                            }
                        },
                        //If there was an error in the connection or delete request, display that it was unsuccessful on the screen
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ProfileActivity.this, "Error occured while deleting user" + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        // Define headers if needed
                        HashMap<String, String> headers = new HashMap<>();
                        // Example headers (uncomment if needed)
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                //finally, if no issues, add the deleted user to queue
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(delete);
            }


        });

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ProfileActivity", "Return button clicked!");
                Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        // TODO: On load, set nameText to user's name
        // TODO: On load, set emailText to user's email
        // TODO: On load, set ageText to user's age
        // TODO: On click of updateProfileButton, update Name, Email, Age, and PW if applicable
        // If a field is blank, assume it does not need to be updated
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userUpdate();
            }
        });
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
            if (passwordEditText.getText().toString().isEmpty()) {
                makeText(getApplicationContext(), "Enter your password to update information", Toast.LENGTH_LONG).show();
            } else {
                // Add authorization header
            }
            requestBody.put("password", passwordEditText.getText().toString());
        } catch (JSONException e) {
            Log.e("JSONError", "Failed to create JSON request body", e);
            makeText(getApplicationContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT, // HTTP method
                URL_JSON_OBJECT + userID, // API URL + userID
                requestBody, // Request body (null for GET request)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log response for debugging
                        Log.d("Volley Response", response.toString());
                        makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log error details
                        Log.e("Volley Error", error.toString());

                        // Display an error message
                        makeText(getApplicationContext(), "Profile update failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Define headers if needed
                HashMap<String, String> headers = new HashMap<>();
                // Create Base64 auth token
                Base64.Encoder encoder = Base64.getEncoder();
                String authToken = encoder.encodeToString(((originalUserEmail + ":" + passwordEditText.getText().toString())).getBytes());
                // Headers
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + authToken);
                Log.d("Auth Token", authToken);
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
