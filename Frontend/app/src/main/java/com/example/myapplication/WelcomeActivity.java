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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import java.util.HashMap;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {

    private static final String URL_LOGIN = "http://coms-3090-066.class.las.iastate.edu:8080/login";
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;
    private Button buttonUserSignin;
    private Button buttonGuestSignin;
    private Button buttonProfessionalSignin;
    private TextView textViewWelcome;
    private ImageView imageViewLogo;
    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private int userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        // View initializations
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.register);
        buttonUserSignin = findViewById(R.id.userSignin);
        buttonGuestSignin = findViewById(R.id.guestSignin);
        buttonProfessionalSignin = findViewById(R.id.professionalSignin);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        buttonUserSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSignIn();
            }
        });

        buttonGuestSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Takes guest users to homepage on guest user button click.
                Intent intent1 = new Intent(WelcomeActivity.this, HomepageActivity.class);
                startActivity(intent1);
                // pass info so system knows it is a guest user
            }
        });

        buttonProfessionalSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: navigate to professionals sign in page when made
                /*
                Intent intent3 = new Intent(WelcomeActivity.this, INSERT PRO PAGE ACTIVITY);
                startActivity(intent3); */
            }
        });
    }

    private void userSignIn() {
        JSONObject requestBody;
        try {
            requestBody = new JSONObject()
                    .put("email", editTextEmail.getText().toString())
                    .put("password", editTextPassword.getText().toString());
        } catch (JSONException e) {
            Log.e("JSONError", "Failed to create JSON request body", e);
            makeText(getApplicationContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, // HTTP method
                URL_LOGIN, // API URL
                requestBody, // Request body (null for GET request)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log response for debugging
                        Log.d("Volley Response", response.toString());
                        userID = response.optInt("id");
                        userFirstName = response.optString("firstName");
                        userLastName = response.optString("lastName");
                        userEmail = response.optString("email");
                        userAge = response.optInt("age");
                        Intent intent2 = new Intent(WelcomeActivity.this, HomepageActivity.class);
                        intent2.putExtra( "userFirstName", userFirstName);
                        intent2.putExtra( "userLastName", userLastName);
                        intent2.putExtra("userEmail", userEmail);
                        intent2.putExtra("userID", userID);
                        intent2.putExtra("userAge", userAge);
                        startActivity(intent2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log error details
                        Log.e("Volley Error", error.toString());

                        // FOR DEBUG
                        makeText(getApplicationContext(), "Failed to load data. Please try again.", Toast.LENGTH_LONG).show();
                        // makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
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