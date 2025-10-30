package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class StudentSignUpActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextAge;
    private EditText editTextPassword;
    private EditText editTextMajor;
    private EditText editTextYearOfStudy;
    private Button buttonUserSignup;

    public StudentSignUpActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_signup);

        // View initializations
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextMajor = findViewById(R.id.editTextMajor);
        editTextYearOfStudy = findViewById(R.id.editTextYearOfStudy);
        buttonUserSignup = findViewById(R.id.userSignup);


        /*
        Implemented logic that upon sign up submission, sends a Post request to the mock Postman server
         */
        buttonUserSignup.setOnClickListener(view -> {
            //hold the user's email and password as a String
            String userEmail = editTextEmail.getText().toString().trim();
            String userPass = editTextPassword.getText().toString().trim();
            String userFirstName = editTextFirstName.getText().toString().trim();
            String userLastName = editTextLastName.getText().toString().trim();
            String userMajor = editTextMajor.getText().toString().trim();
            int userYearOfStudy;
            int userAge;

            Authorization.globalUserEmail = userEmail;
            Authorization.globalPassword = editTextPassword.getText().toString();

            try {
                userAge = Integer.parseInt(editTextAge.getText().toString().trim());
            } catch (NumberFormatException e) {
                editTextAge.setError("Age needs to be an integer.");
                return;
            }

            try {
                userYearOfStudy = Integer.parseInt(editTextYearOfStudy.getText().toString().trim());
            } catch (NumberFormatException e) {
                editTextYearOfStudy.setError("Age needs to be an integer.");
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

            if(userMajor.isEmpty()) {
                editTextMajor.setError("Major field is required");
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
                    response -> {
                        //get response body values from backend
                        try {
                            int id = response.getInt("id");
                            String email = response.getString("email");
                            String firstName = response.getString("firstName");
                            String lastName = response.getString("lastName");
                            int age = response.getInt("age");

                            Log.d("SignUpActivity", "Success ");


                            Authorization.globalUserEmail = email;
                            Authorization.globalPassword = userPass;

                            submitStudentInfo(id);


                            //display message with credentials to show the new user was created
                            Toast.makeText(StudentSignUpActivity.this, "New user created:\n Id: " + id + "\nEmail: " + email +
                                    "\nFirst Name: " + firstName + "\nLast Name: " + lastName + "\nAge: " + age, Toast.LENGTH_LONG).show();

                            //Use intent to go to the next page, in this case the home page
                            Intent intent = new Intent(StudentSignUpActivity.this, GeneralFragmentActivity.class);

                            //send user email and pass and other vals to homepage
                            intent.putExtra("userID", id);
                            intent.putExtra("userEmail", email);
                            intent.putExtra("userPassword", userPass);
                            intent.putExtra("userFirstName", firstName);
                            intent.putExtra("userLastName", lastName);
                            intent.putExtra("userAge", age);
                            intent.putExtra("major", userMajor);
                            intent.putExtra("yearOfStudy", userYearOfStudy);

                            startActivity(intent);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    //Ensure that the server and app communicate and send/retreive info properly, if not display error message
                    error -> Toast.makeText(StudentSignUpActivity.this, "Request failed" , Toast.LENGTH_LONG).show()

            ) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            //finally, add the post to queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(post);

        });



    }

    private void submitStudentInfo(int userId) {
        JSONObject requestStudent = new JSONObject();
        //try to pass the credentials into designated parameters
        try {
            requestStudent.put("major", editTextMajor.getText().toString().trim());
            requestStudent.put("yearOfStudy", Integer.parseInt(editTextYearOfStudy.getText().toString().trim()));
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
                jsonObject -> Log.d("POSTStudent", "Student info sent."),
                volleyError -> Log.e("POSTStudent", "Error occured sending user info.")
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
