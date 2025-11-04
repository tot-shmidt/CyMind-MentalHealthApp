package com.example.myapplication.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CreateChatActivity extends AppCompatActivity {

    private EditText chatNameEdit;
    private RecyclerView professionalsRecycler;
    private Button createChatBtn;
    private Button backBtn;

    private ProfessionalListAdapter adapter;
    private List<User> allProfessionals = new ArrayList<>();
    private List<User> selectedProfessionals = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        chatNameEdit = findViewById(R.id.chatNameEditText);
        professionalsRecycler = findViewById(R.id.professionalListRecyclerView);
        createChatBtn = findViewById(R.id.createChatBtn);
        backBtn = findViewById(R.id.backButton);

        // Placeholder: populate some professionals
        allProfessionals.add(new User(1, "Alice", "Doe", true));
        allProfessionals.add(new User(2, "Bob", "Doe", true));
        allProfessionals.add(new User(3, "Charlie", "Doe", true));

        // Setup RecyclerView
        adapter = new ProfessionalListAdapter(this, allProfessionals);
        professionalsRecycler.setLayoutManager(new LinearLayoutManager(this));
        professionalsRecycler.setAdapter(adapter);

        // Back button
        backBtn.setOnClickListener(v -> finish());

        // Create chat button
        createChatBtn.setOnClickListener(v -> {
            String chatName = chatNameEdit.getText().toString().trim();
            if (chatName.isEmpty() || selectedProfessionals.isEmpty()) {
                Toast.makeText(this, "Enter chat name and select at least one professional", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedProfessionals = adapter.getSelectedProfessionals();

            // Extract selected user IDs
            List<String> memberIds = new ArrayList<>();
            for (User u : selectedProfessionals) {
                memberIds.add(String.valueOf(u.getUserId()));
            }

            // Placeholder for sending to backend
            // TODO: implement HTTP request to create chat
            Toast.makeText(this, "Creating chat: " + chatName + " with " + memberIds.size() + " members", Toast.LENGTH_SHORT).show();

            // Optional: immediately open the new chat (pass a temporary key)
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("chatKey", "temp_chat_key");
            startActivity(intent);
            finish();
        });
    }
}
