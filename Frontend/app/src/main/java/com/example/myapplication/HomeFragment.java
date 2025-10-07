package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    // Declare your views here
    private TextView welcomeMessage;
    private ImageButton buttonProfile;
    private TextView moodMessage;
    private Button moodButtonHappy;
    private Button moodButtonNeutral;
    private Button moodButtonSad;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // init views
        welcomeMessage = rootView.findViewById(R.id.welcomeMessage);
        buttonProfile = rootView.findViewById(R.id.buttonProfile);
        moodMessage = rootView.findViewById(R.id.moodMessage);
        moodButtonHappy = rootView.findViewById(R.id.moodButtonHappy);
        moodButtonNeutral = rootView.findViewById(R.id.moodButtonNeutral);
        moodButtonSad = rootView.findViewById(R.id.moodButtonSad);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get string and int arguments from homepage
        String email = getArguments().getString("email");
        String password = getArguments().getString("password");
        int id = getArguments().getInt("id");

        //display welcome with the user's email
        welcomeMessage.setText("Welcome to Cymind\n");
        welcomeMessage.append(email);

        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            //String usr_email = getActivity().getIntent().getStringExtra("userEmail");
            //pass email, password, and id to home fragment
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            intent.putExtra("id", id);
            startActivity(intent);
        });
    }
}