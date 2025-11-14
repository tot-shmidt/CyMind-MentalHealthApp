package com.example.myapplication;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.chat.ChatManager;

public class ProfessionalHomeFragment extends Fragment {

    // Declare your views here
    private TextView welcomeMessage;
    private ImageButton buttonProfile;
    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private int userAge;
    private String userJobTitle;
    private String userLicenseNumber;
    private static final String APP_API_URL = "http://coms-3090-066.class.las.iastate.edu:8080/";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_professional_home, container, false);

        // init views
        welcomeMessage = rootView.findViewById(R.id.welcomeMessage);
        buttonProfile = rootView.findViewById(R.id.buttonProfile);

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
        userJobTitle = getActivity().getIntent().getStringExtra("userJobTitle");
        userLicenseNumber = getActivity().getIntent().getStringExtra("userLicenseNumber");

        userJobTitle = getActivity().getIntent().getStringExtra("userJobTitle");
        userLicenseNumber = getActivity().getIntent().getStringExtra("userLicenseNumber");

        welcomeMessage.setText("Welcome to CyMind");
        if (!(userID == 0)) {
            welcomeMessage.append(", " + userFirstName + " " + userLastName + "!");
            ChatManager.getInstance().setCurrentUserId(userID); // Set professional user ID
        }

        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfessionalProfileActivity.class);
            intent.putExtra( "userFirstName", userFirstName);
            intent.putExtra( "userLastName", userLastName);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("userID", userID);
            intent.putExtra("userAge", userAge);
            intent.putExtra("userJobTitle", userJobTitle);
            intent.putExtra("userLicenseNumber", userLicenseNumber);
            startActivity(intent);
        });
   }
}
