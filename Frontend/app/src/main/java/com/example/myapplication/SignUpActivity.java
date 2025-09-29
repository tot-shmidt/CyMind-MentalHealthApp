package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;
    private Button buttonUserSignin;
    private Button buttonGuestSignin;
    private Button buttonProfessionalSignin;
    private TextView textViewWelcome;
    private ImageView imageViewLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        // View initializations
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.register);
        buttonUserSignin = findViewById(R.id.userSignin);
        buttonGuestSignin = findViewById(R.id.guestSignin);
        buttonProfessionalSignin = findViewById(R.id.professionalSignin);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: navigate to registration page when made
            }
        });

        buttonUserSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: process signin details and navigate to user homepage when made
                Intent intent = new Intent(SignUpActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        buttonGuestSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: navigate to homepage without user abilities
            }
        });

        buttonProfessionalSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: navigate to professionals sign in page when made
            }
        });
    }
}