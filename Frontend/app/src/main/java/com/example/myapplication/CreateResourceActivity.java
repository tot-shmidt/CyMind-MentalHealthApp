package com.example.myapplication;

import static com.example.myapplication.Authorization.generateAuthToken;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateResourceActivity extends AppCompatActivity {

    // Define global componenet variables

    private EditText titleEditText;
    private EditText category1EditText;
    private EditText category2EditText;
    private EditText category3EditText;
    private EditText contentEditText;
    private Button createButton;
    private Button buttonReturn;
    private static final String APP_API_URL = "https://834f7701-6129-40fc-b41d-30cf356d46b0.mock.pstmn.io/";


    public CreateResourceActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_resource);

        // View initializations
        titleEditText = findViewById(R.id.titleEditText);
        category1EditText = findViewById(R.id.category1EditText);
        category2EditText = findViewById(R.id.category2EditText);
        category3EditText = findViewById(R.id.category3EditText);
        contentEditText = findViewById(R.id.contentEditText);
        createButton = findViewById(R.id.createButton);
        buttonReturn = findViewById(R.id.returnButton);

        Intent intent = getIntent();
        int userID = intent.getIntExtra("userID", -1);
        String userJobTitle = intent.getStringExtra("userJobTitle");
        String userLicenseNumber = intent.getStringExtra("userLicenseNumber");

        buttonReturn.setOnClickListener(view -> {
            Intent intentReturn = new Intent(CreateResourceActivity.this, GeneralFragmentActivity.class);
            startActivity(intentReturn);
        });

        createButton.setOnClickListener(view -> {
            // Grab values from EditTexts
            String articleName = titleEditText.getText().toString().trim();
            String category1 = category1EditText.getText().toString().trim();
            String category2 = category2EditText.getText().toString().trim();
            String category3 = category3EditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            // Validate required fields
            if (articleName.isEmpty()) {
                titleEditText.setError("Title is required");
                return;
            }
            if (content.isEmpty()) {
                contentEditText.setError("Content is required");
                return;
            }

            // Build authors array with current user
            JSONArray authorsArray = new JSONArray();
            try {
                JSONObject authorObj = new JSONObject();
                authorObj.put("userId", userID);
                if (userJobTitle != null) {
                    authorObj.put("jobTitle", userJobTitle);
                }
                if (userLicenseNumber != null) {
                    authorObj.put("licenseNumber", userLicenseNumber);
                }
                authorsArray.put(authorObj);
            } catch (JSONException e) {
                Log.e("CreateResource", "Error building author object", e);
            }

            // Build JSON request
            JSONObject request = new JSONObject();
            try {
                request.put("articleName", articleName);
                request.put("authorId", userID);
                request.put("authors", authorsArray);
                request.put("category1", category1.isEmpty() ? JSONObject.NULL : category1);
                request.put("category2", category2.isEmpty() ? JSONObject.NULL : category2);
                request.put("category3", category3.isEmpty() ? JSONObject.NULL : category3);
                request.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            // POST request URL
            String url = APP_API_URL + "resources/articles/create";

            // Create JsonObjectRequest for PUT
            JsonObjectRequest postRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    request,
                    response -> {
                        Log.d("CreateResource", "Response: " + response);
                        Toast.makeText(CreateResourceActivity.this,
                                "Resource created successfully",
                                Toast.LENGTH_SHORT).show();

                        // Finish activity and return to previous screen
                        finish();
                    },
                    error -> {
                        Log.e("CreateResource", "Volley error: " + error.toString());
                        Toast.makeText(CreateResourceActivity.this,
                                "Failed to create resource",
                                Toast.LENGTH_LONG).show();
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

            // Add request to queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
        });
    }
}

