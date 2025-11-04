package com.example.myapplication.chat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class GroupInfoActivity extends AppCompatActivity {

    private TextView groupNameText;
    private EditText editGroupName;
    private Button saveGroupNameBtn, addMemberBtn, leaveGroupBtn;
    private RecyclerView membersRecyclerView;
    private MemberListAdapter memberListAdapter;
    private List<User> members = new ArrayList<>();

    private int groupId;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        editGroupName = findViewById(R.id.groupNameEditText);
        saveGroupNameBtn = findViewById(R.id.saveGroupNameButton);
        addMemberBtn = findViewById(R.id.addMemberBtn);
        leaveGroupBtn = findViewById(R.id.leaveGroupButton);
        membersRecyclerView = findViewById(R.id.memberListRecyclerView);

        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberListAdapter = new MemberListAdapter(members);
        membersRecyclerView.setAdapter(memberListAdapter);

        // Get group info from Intent
        groupId = getIntent().getIntExtra("groupId", 0);
        groupName = getIntent().getStringExtra("groupName");
        groupNameText.setText(groupName);
        editGroupName.setText(groupName);

        fetchGroupMembers();

        saveGroupNameBtn.setOnClickListener(v -> updateGroupName());
        addMemberBtn.setOnClickListener(v -> openAddMemberDialog());
        leaveGroupBtn.setOnClickListener(v -> leaveGroup());
    }

    private void fetchGroupMembers() {
        // TODO: fetch from backend
        members.clear();
        members.add(new User(1, "Alice", "Student", false));
        members.add(new User(2, "Bob", "Professional", true));
        memberListAdapter.notifyDataSetChanged();
    }

    private void updateGroupName() {
        String newName = editGroupName.getText().toString().trim();
        if (!newName.isEmpty() && !newName.equals(groupName)) {
            groupName = newName;
            groupNameText.setText(groupName);
            // TODO: update backend
            Toast.makeText(this, "Group name updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAddMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Member");
        EditText input = new EditText(this);
        input.setHint("Enter user ID");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String userInput = input.getText().toString().trim();
            if (!userInput.isEmpty()) {
                // TODO: backend add member
                members.add(new User(Integer.parseInt(userInput), "New Member", "Professional", true));
                memberListAdapter.notifyItemInserted(members.size() - 1);
                Toast.makeText(this, "Member added", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void leaveGroup() {
        // TODO: backend leave group
        Toast.makeText(this, "You left the group", Toast.LENGTH_SHORT).show();
        finish();
    }
}
