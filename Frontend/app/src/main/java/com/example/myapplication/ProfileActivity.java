package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Make sure this layout exists and is correct

        Button buttonReturn = findViewById(R.id.returnButton);
        TextView nameText = findViewById(R.id.nameText);
        TextView emailText = findViewById(R.id.emailText);
        TextView ageText = findViewById(R.id.ageText);
        EditText editTextText = findViewById(R.id.nameEditText);
        EditText editTextTextEmailAddress = findViewById(R.id.emailEditText);
        EditText ageEditText = findViewById(R.id.ageEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button updateProfileButton = findViewById(R.id.updateProfileButton);
        Button deleteProfilebutton = findViewById(R.id.deleteProfilebutton);

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        // TODO: On load, set nameText to user's name
        // TODO: On load, set emailText to user's email
        // TODO: On load, set ageText to user's age
        // TODO: On click of updateProfileButton, update Name, Email, Age, and PW if applicablej
        // If a field is blank, assume it does not need to be updated
        // TODO: On click of deleteProfileButton, delete user profile
    }
}
