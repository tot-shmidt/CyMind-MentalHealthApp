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

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String URL_JSON_OBJECT = "https://834f7701-6129-40fc-b41d-30cf356d46b0.mock.pstmn.io/users/update";

    private String userEmail;
    private int userID;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Make sure this layout exists and is correct

        //get the passed email and id from previous pages
        userEmail = getIntent().getStringExtra("userEmail");
        userID = getIntent().getIntExtra("userID", -1);

        //if a userID was not created succesfully, display error
        if (userID == -1) {
            Toast.makeText(this, "User ID not created properly upon sign up", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonReturn = findViewById(R.id.returnButton);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        ageText = findViewById(R.id.ageText);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        ageEditText = findViewById(R.id.ageEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        deleteProfilebutton = findViewById(R.id.deleteProfilebutton);

        /*
        Method for deleting the user (DELETE) and sending request to postman
         */
        deleteProfilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //POSTMAN URL for our DELETE endpoint
                String deleteUserURL = "https://f5fb9954-c023-4687-984d-af55d0cd74f2.mock.pstmn.io/users/" + userID;
                //request conenection from this page

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
            if (!nameEditText.getText().toString().isEmpty()) {
                requestBody.put("name", nameEditText.getText().toString());
            }
            if (!emailEditText.getText().toString().isEmpty()) {
                requestBody.put("email", emailEditText.getText().toString());
            }
            if (!ageEditText.getText().toString().isEmpty()) {
                requestBody.put("age", ageEditText.getText().toString());
            }
            if (!passwordEditText.getText().toString().isEmpty()) {
                requestBody.put("password", passwordEditText.getText().toString());
            }

        } catch (JSONException e) {
            Log.e("JSONError", "Failed to create JSON request body", e);
            makeText(getApplicationContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT, // HTTP method
                URL_JSON_OBJECT, // API URL
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
                // Example headers (uncomment if needed)
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                // Define parameters if needed
                Map<String, String> params = new HashMap<>();
                // Example parameters (uncomment if needed)
                // params.put("param1", "value1");
                // params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to the Volley request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
}
