package com.example.myapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomepageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        int id = getIntent().getIntExtra("id", -1);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new HomeFragment();
        Fragment resourceFragment = new ResourceFragment();
        Fragment chatFragment = new ChatFragment();
        Fragment appointmentFragment = new AppointmentFragment();

        //create bundle of values as arguments
        Bundle bund = new Bundle();
        bund.putString("email", email);
        bund.putString("password", password);
        bund.putInt("id", id);
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