package com.example.myapplication.chat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class AddMemberActivity extends AppCompatActivity {

    private RecyclerView professionalsRv;
    private Button backBtn, addBtn;
    private ProfessionalSelectionAdapter professionalAdapter;
    private List<Professional> availableProfessionals;
    private ChatManager chatManager;
    private String chatId;
    private String chatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        chatManager = ChatManager.getInstance();

        // Get chat info from intent
        chatId = getIntent().getStringExtra("chatId");
        chatName = getIntent().getStringExtra("chatName");

        if (chatId == null) {
            Toast.makeText(this, "Error loading chat info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        professionalsRv = findViewById(R.id.professionalsRv);
        backBtn = findViewById(R.id.backBtn);
        addBtn = findViewById(R.id.addBtn);

        // Setup professionals list (exclude already added members)
        setupProfessionalsList();

        // Back button
        backBtn.setOnClickListener(v -> finish());

        // Add button
        addBtn.setOnClickListener(v -> addSelectedMembers());
    }

    private void setupProfessionalsList() {
        availableProfessionals = loadAvailableProfessionals();

        professionalsRv.setLayoutManager(new LinearLayoutManager(this));
        professionalAdapter = new ProfessionalSelectionAdapter(availableProfessionals);
        professionalsRv.setAdapter(professionalAdapter);
    }

    private List<Professional> loadAvailableProfessionals() {
        // Get current chat room
        ChatRoom chatRoom = chatManager.getChatRoom(chatId);
        List<Integer> existingMemberIds = chatRoom != null ? chatRoom.getProfessionalIds() : new ArrayList<>();

        // Load all professionals and filter out existing members
        List<Professional> allProfessionals = new ArrayList<>();
        allProfessionals.add(new Professional(71, "Garrett Thompson", "Mental Health Counselor"));

        // TODO: Fetch from backend when available

        // Filter out professionals already in the chat
        List<Professional> availableProfessionals = new ArrayList<>();
        for (Professional prof : allProfessionals) {
            if (!existingMemberIds.contains(prof.getProfessionalId())) {
                availableProfessionals.add(prof);
            }
        }

        return availableProfessionals;
    }

    private void addSelectedMembers() {
        List<Integer> selectedProfIds = professionalAdapter.getSelectedProfessionalIds();

        if (selectedProfIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one professional", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to chat room
        ChatRoom chatRoom = chatManager.getChatRoom(chatId);
        if (chatRoom != null) {
            for (Integer profId : selectedProfIds) {
                chatRoom.addProfessional(profId);
            }

            Toast.makeText(this, selectedProfIds.size() + " member(s) added", Toast.LENGTH_SHORT).show();

            // TODO: Send add members request to backend when available

            finish();
        }
    }
}
