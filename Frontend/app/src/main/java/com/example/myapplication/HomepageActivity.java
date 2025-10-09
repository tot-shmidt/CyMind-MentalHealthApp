package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomepageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        String userEmail = getIntent().getStringExtra("userEmail");
        String userPassword = getIntent().getStringExtra("userPassword");
        String userFirstName = getIntent().getStringExtra("userFirstName");
        String userLastName = getIntent().getStringExtra("userLastName");
        int userID = getIntent().getIntExtra("userID", -1);
        int userAge = getIntent().getIntExtra("userAge", -1);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new HomeFragment();
        Fragment resourceFragment = new ResourceFragment();
        Fragment chatFragment = new ChatFragment();
        Fragment appointmentFragment = new AppointmentFragment();

        //create bundle of values as arguments
        Bundle bund = new Bundle();
        bund.putString("userEmail", userEmail);
        bund.putString("userPassword", userPassword);
        bund.putString("userFirstName", userFirstName);
        bund.putString("userLastName", userLastName);
        bund.putInt("userAge", userAge);
        bund.putInt("userID", userID);
        homeFragment.setArguments(bund);

        setCurrentFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                setCurrentFragment(homeFragment);
            } else if (itemId == R.id.resources) {
                setCurrentFragment(resourceFragment);
            } else if (itemId == R.id.chat) {
                setCurrentFragment(chatFragment);
            } else if (itemId == R.id.appt) {
                setCurrentFragment(appointmentFragment);
            }
            return true;
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();

    }
}
