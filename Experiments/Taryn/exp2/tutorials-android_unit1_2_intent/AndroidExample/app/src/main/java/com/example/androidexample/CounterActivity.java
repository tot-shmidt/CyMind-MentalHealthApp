package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CounterActivity extends AppCompatActivity {

    private TextView counterMsg;
    private Button backBtn;     // define back button variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        /* initialize UI elements */
        counterMsg = findViewById(R.id.counter_text);
        backBtn = findViewById(R.id.counter_back_btn);


        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String userEmail = extras.getString("Email");
            counterMsg.setText("Hello " + userEmail + "!");
        }

        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}