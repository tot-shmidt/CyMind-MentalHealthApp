package com.example.myapplication;

import static com.example.myapplication.Authorization.generateAuthToken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UpdateResourceActivity extends AppCompatActivity {

    // Define global componenet variables

    private EditText titleEditText;
    private EditText category1EditText;
    private EditText category2EditText;
    private EditText category3EditText;
    private EditText contentEditText;
    private Button updateButton;
    private Button buttonReturn;
    private static final String APP_API_URL = "https://834f7701-6129-40fc-b41d-30cf356d46b0.mock.pstmn.io/";


    public UpdateResourceActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_resource);

        // View initializations
        titleEditText = findViewById(R.id.titleEditText);
        category1EditText = findViewById(R.id.category1EditText);
        category2EditText = findViewById(R.id.category2EditText);
        category3EditText = findViewById(R.id.category3EditText);
        contentEditText = findViewById(R.id.contentEditText);
        updateButton = findViewById(R.id.updateButton);
        buttonReturn = findViewById(R.id.returnButton);

        // Get intent
        Intent intent = getIntent();
        int resourceId = intent.getIntExtra("resourceId", -1);
        int resourceAuthorId = intent.getIntExtra("resourceAuthorId", -1);
        String resourceAuthors = intent.getStringExtra("resourceAuthors");
        String resourceTitle = intent.getStringExtra("resourceTitle");
        String resourceCategories = intent.getStringExtra("resourceCategories");
        String resourceContent = intent.getStringExtra("resourceContent");

        // Get current professional's info
        int currentUserId = intent.getIntExtra("currentUserId", -1);
        String currentUserJobTitle = intent.getStringExtra("currentUserJobTitle");
        String currentUserLicenseNumber = intent.getStringExtra("currentUserLicenseNumber");

        String[] categories = resourceCategories.split("\\s*,\\s*");

        // Set text fields with resource data
        titleEditText.setText(resourceTitle);
        category1EditText.setText(categories.length > 0 ? categories[0] : null);
        category2EditText.setText(categories.length > 1 ? categories[1] : null);
        category3EditText.setText(categories.length > 2 ? categories[2] : null);
        contentEditText.setText(resourceContent);

        buttonReturn.setOnClickListener(view -> {
            finish();
        });

        updateButton.setOnClickListener(view -> {
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

            // Build authors array - add current user if not already in it
            Set<Integer> authorIds = new HashSet<>();
            if (resourceAuthors != null && !resourceAuthors.trim().isEmpty()) {
                String[] existingAuthors = resourceAuthors.split("\\s*,\\s*");
                for (String authorIdStr : existingAuthors) {
                    try {
                        authorIds.add(Integer.parseInt(authorIdStr.trim()));
                    } catch (NumberFormatException e) {
                        Log.e("UpdateResource", "Invalid author ID: " + authorIdStr);
                    }
                }
            }

            // Add current user if not already in the list
            if (currentUserId != -1) {
                authorIds.add(currentUserId);
            }

            // Build JSONArray of author objects
            JSONArray authorsArray = new JSONArray();
            for (int authorId : authorIds) {
                try {
                    JSONObject authorObj = new JSONObject();
                    authorObj.put("userId", authorId);
                    // Only add job title and license number for current user
                    if (authorId == currentUserId && currentUserJobTitle != null) {
                        authorObj.put("jobTitle", currentUserJobTitle);
                    }
                    if (authorId == currentUserId && currentUserLicenseNumber != null) {
                        authorObj.put("licenseNumber", currentUserLicenseNumber);
                    }
                    authorsArray.put(authorObj);
                } catch (JSONException e) {
                    Log.e("UpdateResource", "Error building author object", e);
                }
            }

            // Build JSON request
            JSONObject request = new JSONObject();
            try {
                request.put("id", resourceId);
                request.put("articleName", articleName);
                request.put("authorId", resourceAuthorId);
                request.put("authors", authorsArray);
                request.put("category1", category1.isEmpty() ? JSONObject.NULL : category1);
                request.put("category2", category2.isEmpty() ? JSONObject.NULL : category2);
                request.put("category3", category3.isEmpty() ? JSONObject.NULL : category3);
                request.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            // PUT request URL
            String url = APP_API_URL + "resources/articles/";

            // Create StringRequest for PUT (handles empty response body)
            StringRequest putRequest = new StringRequest(
                    Request.Method.PUT,
                    url,
                    response -> {
                        Log.d("UpdateResource", "Response: " + response);
                        Toast.makeText(UpdateResourceActivity.this,
                                "Resource updated successfully",
                                Toast.LENGTH_SHORT).show();

                        // Finish activity and return to previous screen
                        finish();
                    },
                    error -> {
                        Log.e("UpdateResource", "Volley error: " + error.toString());
                        Toast.makeText(UpdateResourceActivity.this,
                                "Failed to update resource",
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

                @Override
                public byte[] getBody() {
                    return request.toString().getBytes();
                }
            };

            // Add request to queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(putRequest);

            // send back to resource page!

        });
    }
}

