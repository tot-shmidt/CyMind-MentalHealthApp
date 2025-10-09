package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    // Declare your views here
    private TextView welcomeMessage;
    private ImageButton buttonProfile;
    private TextView moodMessage;
    private SeekBar moodSeekBar;
    private EditText journalEntry;
    private Button submitEntries;

    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private int userAge;

    private String userPassword;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // init views
        welcomeMessage = rootView.findViewById(R.id.welcomeMessage);
        buttonProfile = rootView.findViewById(R.id.buttonProfile);
        moodMessage = rootView.findViewById(R.id.moodMessage);
        moodSeekBar = rootView.findViewById(R.id.moodSeekBar);
        journalEntry = rootView.findViewById(R.id.editTextJournal);
        submitEntries = rootView.findViewById(R.id.submit);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get the passed user info from previous page
        userEmail = getActivity().getIntent().getStringExtra("userEmail");
        userID = getActivity().getIntent().getIntExtra("userID", 0);
        userAge = getActivity().getIntent().getIntExtra("userAge", 0);
        userFirstName = getActivity().getIntent().getStringExtra("userFirstName");
        userLastName = getActivity().getIntent().getStringExtra("userLastName");
        userPassword = getActivity().getIntent().getStringExtra("userPassword");

        welcomeMessage.setText("Welcome to Cymind");
        if (!(userID == 0)) {
            welcomeMessage.append(", " + userFirstName + "!");
        }

        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra( "userFirstName", userFirstName);
            intent.putExtra( "userLastName", userLastName);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("userID", userID);
            intent.putExtra("userAge", userAge);
            intent.putExtra("userPassword", userPassword);

            startActivity(intent);
        });

        // Set a listener to handle changes and enforce discrete positions
        moodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Called anytime progress bar changes
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when the user starts touching the seek bar, required method
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Called when the user stops touching the seek bar
                // Send user data to the next page as well as the mood input
                Intent intent = new Intent(getActivity(), MoodActivity.class);
                intent.putExtra( "userFirstName", userFirstName);
                intent.putExtra( "userLastName", userLastName);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("userID", userID);
                intent.putExtra("userAge", userAge);
                intent.putExtra("mood", moodSeekBar.getProgress());
                startActivity(intent);
            }
        });

        submitEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new page to display mood and journal entries
                //post requests for journal and mood entries
            }
        });

    }
}