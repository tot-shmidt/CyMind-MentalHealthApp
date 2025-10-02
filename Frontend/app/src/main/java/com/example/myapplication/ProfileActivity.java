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
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private String userEmail;
    private int userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Make sure this layout exists and is correct

        //get the passed email and id from previous pages
        userEmail = getIntent().getStringExtra("userEmail");
        userID = getIntent().getIntExtra("userID", -1);

        //if a userID was not created succesfully, display error
        if(userID == -1) {
            Toast.makeText(this, "User ID not created properly upon sign up", Toast.LENGTH_SHORT).show();
            return;
        }

        Button buttonReturn = findViewById(R.id.returnButton);
        TextView nameText = findViewById(R.id.nameText);
        TextView emailText = findViewById(R.id.emailText);
        TextView ageText = findViewById(R.id.ageText);
        EditText editTextText = findViewById(R.id.nameEditText);
        EditText editTextTextEmailAddress = findViewById(R.id.emailEditText);
        EditText ageEditText = findViewById(R.id.ageEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button updateProfileButton = findViewById(R.id.updateProfileButton);
        Button deleteProfilebutton = findViewById(R.id.deleteProfilebutton);

        /*
        Method for deleting the user (DELETE) and sending request to postman
         */
        deleteProfilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //POSTMAN URL for our DELETE endpoint
                String deleteUserURL = "https://f5fb9954-c023-4687-984d-af55d0cd74f2.mock.pstmn.io/users/" + userID;
                //request conenection from this page
                RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);

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
                        });

                        //finally, if no issues, add the deleted user to queue
                        queue.add(delete);
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
        // TODO: On click of updateProfileButton, update Name, Email, Age, and PW if applicablej
        // If a field is blank, assume it does not need to be updated
        // TODO: On click of deleteProfileButton, delete user profile
    }
}
