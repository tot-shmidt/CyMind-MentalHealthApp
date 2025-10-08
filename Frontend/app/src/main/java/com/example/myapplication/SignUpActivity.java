package com.example.myapplication;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {

    private EditText editTextFirstName;

    private EditText editTextLastName;
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
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextAge = findViewById(R.id.editTextAge);

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


                //hold the user's email and password as a String
                String userEmail = editTextEmail.getText().toString().trim();
                String userPass = editTextPassword.getText().toString().trim();
                String userFirstName = editTextFirstName.getText().toString().trim();
                String userLastName = editTextLastName.getText().toString().trim();

                int userAge;
                try {
                    userAge = Integer.parseInt(editTextAge.getText().toString().trim());
                } catch (NumberFormatException e) {
                    editTextAge.setError("Age needs to be an integer.");
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

                //validate if user password is at least 8 characters
                if(userPass.length() < 8) {
                    editTextPassword.setError("Password must be at least 8 characters");
                    return;
                }

                //ensure user enters their name
                if(userFirstName.isEmpty()) {
                    editTextFirstName.setError("First Name field is required");
                    return;
                }

                if(userLastName.isEmpty()) {
                    editTextLastName.setError("Last Name field is required");
                    return;
                }


                JSONObject request = new JSONObject();
                //try to pass the credentials into designated parameters
                try {
                    request.put("email", userEmail);
                    request.put("password", userPass);
                    request.put("firstName", userFirstName);
                    request.put("lastName", userLastName);
                    request.put("age", userAge);
                }
                //catch it if errors occur when requesting
                catch(JSONException e) {
                    e.printStackTrace();
                    return;
                }

                //connect to server with sign up POST endpoint (created from backend)
                String postUrl = "http://coms-3090-066.class.las.iastate.edu:8080/signup";


                //request to post info as json
                JsonObjectRequest post = new JsonObjectRequest(Request.Method.POST, postUrl, request,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //get response body values from backend
                                try {
                                    int id = response.getInt("id");
                                    String email = response.getString("email");
                                    String password = userPass;
                                    String firstName = response.getString("firstName");
                                    String lastName = response.getString("lastName");
                                    int age = response.getInt("age");

                                    Log.d("SignUpActivity", "Success ");


                                    Authorization.globalUserEmail = email;
                                    Authorization.globalPassword = password;

                                    submitStudentInfo(id, email, password);


                                    //display message with credentials to show the new user was created
                                    Toast.makeText(SignUpActivity.this, "New user created:\n Id: " + id + "\nEmail: " + email +
                                            "\nFirst Name: " + firstName + "\nLast Name: " + lastName + "\nAge: " + age, Toast.LENGTH_LONG).show();

                                    //Use intent to go to the next page, in this case the home page
                                    Intent intent = new Intent(SignUpActivity.this, HomepageActivity.class);

                                    //send user email and pass and other vals to homepage
                                    intent.putExtra("userID", id);
                                    intent.putExtra("userEmail", email);
                                    intent.putExtra("userPassword", password);
                                    intent.putExtra("userFirstName", firstName);
                                    intent.putExtra("userLastName", lastName);
                                    intent.putExtra("userAge", age);

                                    startActivity(intent);

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }


                            }
                        },
                        //Ensure that the server and app communicate and send/retreive info properly, if not display error message
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(SignUpActivity.this, "Request failed" , Toast.LENGTH_LONG).show();
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

    private void submitStudentInfo(int userId, String email, String password) {
        String major = "Software Engineering";
        int yearOfStudy = 3;


        JSONObject requestStudent = new JSONObject();
        //try to pass the credentials into designated parameters
        try {
            requestStudent.put("major", major);
            requestStudent.put("yearOfStudy", yearOfStudy);
            requestStudent.put("userId", userId);
        }
        //catch it if errors occur when requesting
        catch(JSONException e) {
            e.printStackTrace();
            return;
        }

        //connect to server with sign up POST endpoint (created from backend)
        String postStudentURL = "http://coms-3090-066.class.las.iastate.edu:8080/users/student";

        JsonObjectRequest postStudent = new JsonObjectRequest(Request.Method.POST, postStudentURL, requestStudent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("POSTStudent", "Student info sent.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("POSTStudent", "Error occured sending user info.");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + Authorization.generateAuthToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postStudent);

    }
}
