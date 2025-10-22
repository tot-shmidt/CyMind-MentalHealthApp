package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStudent = findViewById(R.id.buttonStudent);
        Button buttonProf = findViewById(R.id.buttonProf);

        buttonStudent.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        });

        buttonProf.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LogInProfessional.class);
            startActivity(intent);
        });
    }
}
