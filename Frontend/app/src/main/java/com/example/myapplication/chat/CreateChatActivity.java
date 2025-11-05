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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateChatActivity extends AppCompatActivity {

    private static final String APP_API_URL = "http://coms-3090-066.class.las.iastate.edu:8080/";

    private EditText chatNameEt, serverUrlEt;
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
        serverUrlEt = findViewById(R.id.serverUrlEt);
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
        // String url = APP_API_URL + "users/professional?name=searchName&num=10";

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
        String serverUrl = serverUrlEt.getText().toString().trim();

        if (chatName.isEmpty()) {
            Toast.makeText(this, "Please enter a chat name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (serverUrl.isEmpty()) {
            Toast.makeText(this, "Please enter server URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected professionals
        List<Integer> selectedProfIds = professionalAdapter.getSelectedProfessionalIds();

        Log.d("CreateChatActivity", "Selected professional IDs: " + selectedProfIds.toString());

        if (selectedProfIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one professional", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique chat ID
        String chatId = "chat_" + UUID.randomUUID().toString().substring(0, 8);

        // Build WebSocket URL (adjust format based on your backend)
        int username = chatManager.getCurrentUserId();
        String wsUrl = serverUrl + "/" + chatId + "/" + username;

        Log.d("CreateChatActivity", "Creating chat: " + chatName + " with ID: " + chatId);
        Log.d("CreateChatActivity", "Professional IDs: " + selectedProfIds.toString());
        Log.d("CreateChatActivity", "Current user ID: " + username);

        // Create chat room
        ChatRoom newChat = new ChatRoom(chatId, chatName, selectedProfIds, wsUrl);
        newChat.setCreatorId(username); // Set the student who created it
        chatManager.addChatRoom(newChat);

        Log.d("CreateChatActivity", "Chat added to manager. Total chats now: " + chatManager.getAllChatRooms().size());

        // Start WebSocket service
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        serviceIntent.setAction("CONNECT");
        serviceIntent.putExtra("key", chatId);
        serviceIntent.putExtra("url", wsUrl);
        startService(serviceIntent);

        Toast.makeText(this, "Chat created successfully!", Toast.LENGTH_SHORT).show();

        // Open the new chat
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("chatName", chatName);
        startActivity(intent);

        finish();
    }
}