package com.example.myapplication;

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
        Log.d("ProfileActivity", "onCreate: ProfileActivity started successfully!");
        TextView TextView = findViewById(R.id.textView); // If R.id.userName doesn't exist in activity_profile.xml
        TextView.setText("Test"); // This will cause a NullPointerException

    }
}
