package com.example.myapplication;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import java.util.HashMap;
import java.util.Map;


import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity implements WebSocketListener{

    private static final String URL_LOGIN = "http://coms-3090-066.class.las.iastate.edu:8080/login";
    private EditText editTextEmail;
    private EditText editTextPassword;
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
        Button buttonRegister = findViewById(R.id.register);
        Button buttonUserSignin = findViewById(R.id.userSignin);
        Button buttonGuestSignin = findViewById(R.id.guestSignin);



        buttonRegister.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, StudentSignUpActivity.class);
            startActivity(intent);
        });

        buttonUserSignin.setOnClickListener(view -> userSignIn());

        buttonGuestSignin.setOnClickListener(view -> {
            // Takes guest users to homepage on guest user button click.
            Intent intent1 = new Intent(WelcomeActivity.this, GeneralFragmentActivity.class);
            startActivity(intent1);
            // pass info so system knows it is a guest user
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
                response -> {
                    // Log response for debugging
                    Log.d("Volley Response", response.toString());
                    userID = response.optInt("id");
                    userFirstName = response.optString("firstName");
                    userLastName = response.optString("lastName");
                    userEmail = response.optString("email");
                    userAge = response.optInt("age");
                    Authorization.globalUserEmail = userEmail;
                    Authorization.globalPassword = editTextPassword.getText().toString();
                    Intent intent2 = new Intent(WelcomeActivity.this, GeneralFragmentActivity.class);

                    String webSocketURL = "ws://coms-3090-066.class.las.iastate.edu:8080/notifications/" + userID;
                    WebSocketManager.getInstance().setWebSocketListener(WelcomeActivity.this);
                    WebSocketManager.getInstance().connectWebSocket(webSocketURL);

                    intent2.putExtra( "userFirstName", userFirstName);
                    intent2.putExtra( "userLastName", userLastName);
                    intent2.putExtra("userEmail", userEmail);
                    intent2.putExtra("userPassword", editTextPassword.getText().toString());
                    intent2.putExtra("userID", userID);
                    intent2.putExtra("userAge", userAge);
                    startActivity(intent2);
                },
                error -> {
                    // Log error details
                    Log.e("Volley Error", error.toString());

                    // FOR DEBUG
                    // makeText(getApplicationContext(), "Failed to load data. Please try again.", Toast.LENGTH_LONG).show();
                    makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Define headers if needed
                HashMap<String, String> headers = new HashMap<>();
                // Example headers (uncomment if needed)
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                // Define parameters if needed
                // Example parameters (uncomment if needed)
                // params.put("param1", "value1");
                // params.put("param2", "value2");
                return new HashMap<>();
            }
        };

        // Adding request to the Volley request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected to articles endpoint");
    }

    @Override
    public void onWebSocketMessage(String message) {
        Log.d("WebSocket", "New message: " + message);

        WebSocketManager.getInstance().addNotification(message);

        try {
            JSONObject json = new JSONObject(message);

            String notif_type = json.optString("type", "ARTICLE");

            String title;
            String body;

            if (notif_type.equals("APPOINTMENT_BOOKED")) {
                title = json.optString("title", "Appointment");
                String location = json.optString("location", "");
                String startTime = json.optString("startTime", "");
                body = "New appointment booked at " + location + " on " + startTime;
            } else {

                title = json.optString("ArticleName", "New Article");
                body = json.optString("message", "A new article is available!");
            }

            Notifications.showNotification(getApplicationContext(), title, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Closed: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error", ex);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Disconnect WebSocket when leaving this activity
//        //WebSocketManager.getInstance().disconnectWebSocket();
//    }
}