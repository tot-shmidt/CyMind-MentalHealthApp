package com.example.myapplication;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AppointmentListFragment extends Fragment {

    private Button makeAppointment;
    private int studentId;
    private String userEmail;
    private String userPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_appointment_list, container, false);

        Bundle bund = getArguments();
        assert bund != null;
        studentId = bund.getInt("userID", -1);
        userEmail = bund.getString("userEmail");
        userPassword = bund.getString("userPassword");

        makeAppointment = v.findViewById(R.id.make_app_button);

        makeAppointment.setOnClickListener(view -> {
            StudentAppointmentFragment fragment = new StudentAppointmentFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("userID", studentId);
            bundle.putString("userEmail", userEmail);
            bundle.putString("userPassword", userPassword);
            fragment.setArguments(bundle);

            FragmentTransaction next = requireActivity().getSupportFragmentManager().beginTransaction();

            next.replace(R.id.flFragment, fragment);
            next.addToBackStack(null);
            next.commit();
        });

        return v;
    }

}
