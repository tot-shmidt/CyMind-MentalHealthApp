package com.example.myapplication.chat;

import static com.example.myapplication.Authorization.generateAuthToken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.myapplication.R;
import com.example.myapplication.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CreateChatActivity extends AppCompatActivity {

    private static final String APP_API_URL = "http://coms-3090-066.class.las.iastate.edu:8080/";
    private static final String WS_URL = "ws://coms-3090-066.class.las.iastate.edu:8080/chat";

    private EditText chatNameEt;
    private RecyclerView professionalsRv;
    private Button createBtn, cancelBtn;
    private ProfessionalSelectionAdapter professionalAdapter;
    private List<Professional> availableProfessionals;
    private ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        chatManager = ChatManager.getInstance();

        // Initialize UI elements
        chatNameEt = findViewById(R.id.chatNameEt);
        professionalsRv = findViewById(R.id.professionalsRv);
        createBtn = findViewById(R.id.createBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // Setup professionals list
        setupProfessionalsList();

        // Create button listener
        createBtn.setOnClickListener(v -> createChat());

        // Cancel button listener
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void setupProfessionalsList() {
        // Initialize with empty list
        availableProfessionals = new ArrayList<>();

        professionalsRv.setLayoutManager(new LinearLayoutManager(this));
        professionalAdapter = new ProfessionalSelectionAdapter(availableProfessionals);
        professionalsRv.setAdapter(professionalAdapter);

        // Load professionals from backend
        loadAvailableProfessionals();
    }

    private void loadAvailableProfessionals() {
        // Temporary hardcoded professionals (for testing until backend is ready)
        List<Professional> professionals = new ArrayList<>();
        professionals.add(new Professional(59, "Garrett Thompson", "Mental Health Counselor"));
        professionals.add(new Professional(1, "Dr. Sarah Johnson", "Licensed Therapist"));
        professionals.add(new Professional(2, "Dr. Michael Chen", "Clinical Psychologist"));
        professionals.add(new Professional(3, "Dr. Emily Rodriguez", "Counselor"));

        availableProfessionals.clear();
        availableProfessionals.addAll(professionals);
        professionalAdapter.notifyDataSetChanged();

        // TODO: Uncomment when backend is ready
        // fetchProfessionalsFromBackend();
    }

    /**
     * Fetches all professionals from the backend API.
     * Backend endpoint: GET /users/professional?name={string}&num={int}
     * Returns: 200: [{userId, firstName, lastName}, ..., {userId, firstName, lastName}]
     * If no parameters are provided, it returns all professionals.
     */
    private void fetchProfessionalsFromBackend() {
        String url = APP_API_URL + "users/professional";

        // Optional: Add query parameters
        // String url = APP_API_URL + TBD?;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null, // GET request has no body
            response -> {
                Log.d("Volley Response", response.toString());
                List<Professional> professionals = new ArrayList<>();

                try {
                    // Parse each professional from the response array
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject profObj = response.getJSONObject(i);

                        int userId = profObj.getInt("userId");
                        String firstName = profObj.getString("firstName");
                        String lastName = profObj.getString("lastName");

                        // Create full name
                        String fullName = firstName + " " + lastName;

                        // Create Professional object with a default specialization
                        // Note: Backend doesn't return specialization, so using generic title
                        Professional prof = new Professional(userId, fullName, "Mental Health Professional");
                        professionals.add(prof);
                    }

                    // Update the adapter with new data
                    availableProfessionals.clear();
                    availableProfessionals.addAll(professionals);
                    professionalAdapter.notifyDataSetChanged();

                    Log.d("CreateChatActivity", "Loaded " + professionals.size() + " professionals");

                } catch (JSONException e) {
                    Log.e("CreateChatActivity", "JSON parse error", e);
                    Toast.makeText(
                        getApplicationContext(),
                        "Failed to parse professionals data",
                        Toast.LENGTH_LONG
                    ).show();
                }
            },
            error -> {
                Log.e("Volley Error", error.toString());
                Toast.makeText(
                    getApplicationContext(),
                    "Failed to load professionals. Using default list.",
                    Toast.LENGTH_LONG
                ).show();

                // Fall back to hardcoded list on error
                List<Professional> fallbackProfessionals = new ArrayList<>();
                fallbackProfessionals.add(new Professional(59, "Garrett Thompson", "Mental Health Counselor"));
                fallbackProfessionals.add(new Professional(1, "Dr. Sarah Johnson", "Licensed Therapist"));
                fallbackProfessionals.add(new Professional(2, "Dr. Michael Chen", "Clinical Psychologist"));
                fallbackProfessionals.add(new Professional(3, "Dr. Emily Rodriguez", "Counselor"));

                availableProfessionals.clear();
                availableProfessionals.addAll(fallbackProfessionals);
                professionalAdapter.notifyDataSetChanged();
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
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void createChat() {
        String chatName = chatNameEt.getText().toString().trim();

        if (chatName.isEmpty()) {
            Toast.makeText(this, "Please enter a chat name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected professionals
        List<Integer> selectedProfIds = professionalAdapter.getSelectedProfessionalIds();

        Log.d("CreateChatActivity", "Selected professional IDs: " + selectedProfIds.toString());

        if (selectedProfIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one professional", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create request body: {professionalIds, studentIds, groupName}
        int userId = chatManager.getCurrentUserId();

        try {
            JSONObject requestBody = new JSONObject();

            // Add professionalIds array
            org.json.JSONArray professionalIdsArray = new org.json.JSONArray();
            for (Integer profId : selectedProfIds) {
                professionalIdsArray.put(profId);
            }
            requestBody.put("professionalIds", professionalIdsArray);

            // Add studentIds array (current user only)
            org.json.JSONArray studentIdsArray = new org.json.JSONArray();
            studentIdsArray.put(userId);
            requestBody.put("studentIds", studentIdsArray);

            // Add groupName
            requestBody.put("groupName", chatName);

            Log.d("CreateChatActivity", "Creating chat with body: " + requestBody.toString());

            // POST /chat/groups
            String url = APP_API_URL + "chat/groups";
            Log.d("CreateChatActivity", "POST URL: " + url);
            Log.d("CreateChatActivity", "User ID: " + userId);

            com.android.volley.toolbox.JsonObjectRequest jsonObjectRequest = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try {
                        // Parse response: {id, professionalIds, studentIds, groupName, messageIds, createdOn}
                        String groupId = response.getString("id");
                        String groupName = response.getString("groupName");

                        // Parse professionalIds array from response
                        List<Integer> professionalIds = new ArrayList<>();
                        org.json.JSONArray profIdsArray = response.optJSONArray("professionalIds");
                        if (profIdsArray != null) {
                            for (int i = 0; i < profIdsArray.length(); i++) {
                                professionalIds.add(profIdsArray.getInt(i));
                            }
                        }

                        // Parse studentIds array from response
                        List<Integer> studentIds = new ArrayList<>();
                        org.json.JSONArray studentIdsResponseArray = response.optJSONArray("studentIds");
                        if (studentIdsResponseArray != null) {
                            for (int i = 0; i < studentIdsResponseArray.length(); i++) {
                                studentIds.add(studentIdsResponseArray.getInt(i));
                            }
                        }

                        Log.d("CreateChatActivity", "Chat created successfully with ID: " + groupId);
                        Log.d("CreateChatActivity", "ProfessionalIds: " + professionalIds);
                        Log.d("CreateChatActivity", "StudentIds: " + studentIds);

                        // Build WebSocket URL: ws://server/chat/{groupId}/{userId}
                        String wsUrl = WS_URL + "/" + groupId + "/" + userId;

                        // Create chat room with both professionalIds and studentIds
                        ChatRoom newChat = new ChatRoom(groupId, groupName, professionalIds, studentIds, wsUrl);
                        newChat.setCreatorId(userId);
                        chatManager.addChatRoom(newChat);

                        // Start WebSocket service
                        Intent serviceIntent = new Intent(this, WebSocketService.class);
                        serviceIntent.setAction("CONNECT");
                        serviceIntent.putExtra("key", groupId);
                        serviceIntent.putExtra("url", wsUrl);
                        startService(serviceIntent);

                        Toast.makeText(this, "Chat created successfully!", Toast.LENGTH_SHORT).show();

                        // Open the new chat
                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra("chatId", groupId);
                        intent.putExtra("chatName", groupName);
                        startActivity(intent);

                        finish();

                    } catch (JSONException e) {
                        Log.e("CreateChatActivity", "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error creating chat", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CreateChatActivity", "Error creating chat: " + error.toString());

                    // Detailed error logging
                    if (error.networkResponse != null) {
                        Log.e("CreateChatActivity", "Status Code: " + error.networkResponse.statusCode);
                        Log.e("CreateChatActivity", "Response Data: " + new String(error.networkResponse.data));
                        Log.e("CreateChatActivity", "Headers: " + error.networkResponse.headers);
                    } else {
                        Log.e("CreateChatActivity", "Network Response is null - possible network issue");
                    }

                    if (error.getCause() != null) {
                        Log.e("CreateChatActivity", "Cause: " + error.getCause().getMessage());
                    }

                    Toast.makeText(this, "Failed to create chat: " + error.toString(), Toast.LENGTH_LONG).show();
                }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    String authToken = generateAuthToken();
                    headers.put("Authorization", "Basic " + authToken);
                    Log.d("CreateChatActivity", "Request Headers: " + headers.toString());
                    Log.d("CreateChatActivity", "Auth Token (first 20 chars): " + (authToken.length() > 20 ? authToken.substring(0, 20) : authToken));
                    return headers;
                }
            };

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            Log.e("CreateChatActivity", "Error building request: " + e.getMessage());
            Toast.makeText(this, "Error creating chat", Toast.LENGTH_SHORT).show();
        }
    }
}