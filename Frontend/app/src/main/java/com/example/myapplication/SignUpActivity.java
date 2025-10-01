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

        buttonUserSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: navigate to registration page when made

                String userEmail = editTextEmail.getText().toString().trim();
                String userPass = editTextPassword.getText().toString().trim();

                if(userEmail.isEmpty()) {
                    editTextEmail.setError("Email field is required");
                    return;
                }

                if(userPass.isEmpty()) {
                    editTextPassword.setError("Password field is required");
                    return;
                }
                JSONObject request = new JSONObject();
                try {
                    request.put("email", userEmail);
                    request.put("password", userPass);
                }
                catch(JSONException e) {
                    e.printStackTrace();
                    return;
                }

                String postUrl = "https://f5fb9954-c023-4687-984d-af55d0cd74f2.mock.pstmn.io/users";
                RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);

                JsonObjectRequest post = new JsonObjectRequest(Request.Method.POST, postUrl, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String postedEmail = response.getString("email");
                            String postedPass = response.getString("password");

                            Toast.makeText(SignUpActivity.this, "New user with email:" + postedEmail + "Password is: " + postedPass,
                                    Toast.LENGTH_LONG).show();
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError err) {
                                Toast.makeText(SignUpActivity.this, "Error request: " + err.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                queue.add(post);

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