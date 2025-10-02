package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextAge;
    private EditText editTextPassword;
    private Button buttonRegister;
    private Button buttonUserSignin;
    private Button buttonGuestSignin;
    private Button buttonProfessionalSignin;
    private TextView textViewWelcome;
    private ImageView imageViewLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        // View initializations
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAge = findViewById(R.id.editTextAge);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.register);
        buttonUserSignin = findViewById(R.id.userSignup);
        buttonGuestSignin = findViewById(R.id.guestSignin);
        buttonProfessionalSignin = findViewById(R.id.professionalSignin);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*
        Implemented logic that upon sign up submission, sends a Post request to the mock Postman server
         */
        buttonUserSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: navigate to registration page when made

                String userName = editTextName.getText().toString().trim();
                //hold the user's email and password as a String
                String userEmail = editTextEmail.getText().toString().trim();
                String userPass = editTextPassword.getText().toString().trim();

                String userAge = editTextAge.getText().toString().trim();

                //ensure user enters their name
                if(userName.isEmpty()) {
                    editTextEmail.setError("Name field is required");
                    return;
                }
                //ensure that the user enters an email since it is required, if a user tries to sign up
                //without entering, it will not work
                if(userEmail.isEmpty()) {
                    editTextEmail.setError("Email field is required");
                    return;
                }

                //ensure user enters a pass, will come up with error to tell user to use a password
                if(userPass.isEmpty()) {
                    editTextPassword.setError("Password field is required");
                    return;
                }

                //ensure user enters an age
                if(userAge.isEmpty()) {
                    editTextEmail.setError("Age field is required");
                    return;
                }

                //validate if user password is at least 8 characters
                if(userPass.length() < 8) {
                    editTextPassword.setError("Password must be at least 8 characters");
                    return;
                }


                //for now, come up with a random id for the user that will be assigned upon sign in
                int userID = (int) (Math.random() * 100);
                JSONObject request = new JSONObject();
                //try to send the credentials
                try {
                    request.put("email", userEmail);
                    request.put("password", userPass);
                    request.put("id", userID);
                }
                //catch it if errors occur when retreiving info
                catch(JSONException e) {
                    e.printStackTrace();
                    return;
                }

                //POSTMAN mock server connection plus endpoint (POST)
                String postUrl = "https://f5fb9954-c023-4687-984d-af55d0cd74f2.mock.pstmn.io/users";
                //We will be using volley for roundtrips, so set this request up early


                //request to post info as json
                JsonObjectRequest post = new JsonObjectRequest(Request.Method.POST, postUrl, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //get the email and password from json in mock server

                            //if POST was sucessful , display message on screen that the user was created.
                            //For now will be dummy info since this is a mock server and we have not connected backend
                            Toast.makeText(SignUpActivity.this, "New user created successfully", Toast.LENGTH_LONG).show();

                            //Use intent to go to the next page, in this case the home page
                            Intent intent = new Intent(SignUpActivity.this, HomepageActivity.class);

                            //send user email and pass to homepage
                            intent.putExtra("userEmail", userEmail);
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                    }
                },
                        //Ensure that the postman and app communicate and send/retreive info properly, if not display error message
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError err) {
                                Toast.makeText(SignUpActivity.this, "Error request: " + err.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                ) {
                    @Override
                    public java.util.Map<String, String> getHeaders() {
                        java.util.HashMap<String, String> headers = new java.util.HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }

                };
                //finally, add the post to queue
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(post);

            }
        });

        buttonGuestSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: navigate to homepage without user abilities
            }
        });

        buttonProfessionalSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: navigate to professionals sign in page when made
            }
        });
    }
}