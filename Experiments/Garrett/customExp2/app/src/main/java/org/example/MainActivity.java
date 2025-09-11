package org.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // links to main activity xml file

        // initialize UI elements
        welcomeMessage = findViewById(R.id.main_welcome_msg);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment firstFragment = new FirstFragment();
        Fragment secondFragment = new SecondFragment();
        Fragment thirdFragment = new ThirdFragment();

        setCurrentFragment(firstFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the ID once

            if (itemId == R.id.resource_menu) {
                setCurrentFragment(firstFragment);
            } else if (itemId == R.id.contact_menu) {
                setCurrentFragment(secondFragment);
            } else if (itemId == R.id.locations_menu) {
                setCurrentFragment(thirdFragment);
            }
            // You might want a default case or to return false if no item was handled,
            // though for BottomNavigationView, returning true usually indicates the selection was handled.
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