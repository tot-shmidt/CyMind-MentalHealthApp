package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class UpdateResourceActivity extends AppCompatActivity {

    // Define global componenet variables

    private EditText titleEditText;
    private EditText category1EditText;
    private EditText category2EditText;
    private EditText category3EditText;
    private EditText contentEditText;
    private Button updateButton;
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

        // Get intent
        Intent intent = getIntent();
        int resourceId = intent.getIntExtra("resourceId", -1);
        int resourceAuthorId = intent.getIntExtra("resourceAuthorId", -1);
        String resourceAuthorName = intent.getStringExtra("resourceAuthorName");
        String resourceTitle = intent.getStringExtra("resourceTitle");
        String resourceCategories = intent.getStringExtra("resourceCategories");
        String resourceContent = intent.getStringExtra("resourceContent");

        String[] categories = resourceCategories.split("\\s*,\\s*");

        // Set text fields with resource data
        titleEditText.setText(resourceTitle);
        category1EditText.setText(categories.length > 0 ? categories[0] : null);
        category2EditText.setText(categories.length > 1 ? categories[1] : null);
        category3EditText.setText(categories.length > 2 ? categories[2] : null);
        contentEditText.setText(resourceContent);
//
//        // TODO update to update resource
//        updateButton.setOnClickListener(view -> {
//            // Grab values from EditTexts
//            String articleName = titleEditText.getText().toString().trim();
//            String category1 = category1EditText.getText().toString().trim();
//            String category2 = category2EditText.getText().toString().trim();
//            String category3 = category3EditText.getText().toString().trim();
//            String content = contentEditText.getText().toString().trim();
//
//            // Validate required fields
//            if (articleName.isEmpty()) {
//                titleEditText.setError("Title is required");
//                return;
//            }
//            if (content.isEmpty()) {
//                contentEditText.setError("Content is required");
//                return;
//            }
//
//            // Build JSON request
//            JSONObject request = new JSONObject();
//            try {
//                request.put("articleName", articleName);
//                request.put("authorId", intent.getIntExtra("resourceAuthorId", -1));
//                request.put("category1", category1.isEmpty() ? JSONObject.NULL : category1);
//                request.put("category2", category2.isEmpty() ? JSONObject.NULL : category2);
//                request.put("category3", category3.isEmpty() ? JSONObject.NULL : category3);
//                request.put("content", content);
//            } catch (JSONException e) {
//                e.printStackTrace();
//                return;
//            }
//
//            // PUT request URL
//            String url = APP_API_URL + "resources/articles/" + resourceId;
//
//            // Create JsonObjectRequest for PUT
//            JsonObjectRequest putRequest = new JsonObjectRequest(
//                    Request.Method.PUT,
//                    url,
//                    request,
//                    response -> {
//                        Log.d("UpdateResource", "Response: " + response);
//                        Toast.makeText(UpdateResourceActivity.this,
//                                "Resource updated successfully",
//                                Toast.LENGTH_SHORT).show();
//
//                        // Finish activity and return to previous screen
//                        finish();
//                    },
//                    error -> {
//                        Log.e("UpdateResource", "Volley error: " + error.toString());
//                        Toast.makeText(UpdateResourceActivity.this,
//                                "Failed to update resource",
//                                Toast.LENGTH_LONG).show();
//                    }
//            ) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    HashMap<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    // headers.put("Authorization", "Basic " + generateAuthToken());
//                    return headers;
//                }
//            };
//
//            // Add request to queue
//            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(putRequest);
//        });
    }
}

