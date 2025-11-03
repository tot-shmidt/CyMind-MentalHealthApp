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
import android.widget.TextView;
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

public class ProfessionalAppointmentFragment extends Fragment {

    private Button makeAppointment;
    private int studentId;
    private String userEmail;
    private String userPassword;
    private TextView appointmentInfo;

    private int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_appointment_list, container, false);

        appointmentInfo = v.findViewById(R.id.appointment_info);

        Bundle bund = getArguments();
        assert bund != null;
        studentId = bund.getInt("userID", -1);
        userEmail = bund.getString("userEmail");
        userPassword = bund.getString("userPassword");
        id = bund.getInt("id", -1);

        Authorization.globalUserEmail = userEmail;
        Authorization.globalPassword = userPassword;

        String getURL = "http://coms-3090-066.class.las.iastate.edu:8080/appointments/" + id;

        JsonObjectRequest get = new JsonObjectRequest(Request.Method.GET, getURL, null,
                response -> {
                    try {
                        int id = response.getInt("id");
                        String startTime = response.getString("startTime");
                        int duration = response.getInt("duration");
                        int appointmentGroupId = response.getInt("appointmentGroupId");
                        String status = response.getString("status");

                        String location = response.isNull("location") ? "" : response.getString("location");
                        String title = response.isNull("title") ? "" : response.getString("title");
                        String description = response.isNull("description") ? "" : response.getString("description");


                        String display = "Appointment ID: " + id +
                                "\nTitle: " + title +
                                "\nTime: " + startTime +
                                "\nDuration: " + duration + " min" +
                                "\nLocation: " + location +
                                "\nStatus: " + status +
                                "\nDescription: " + description;

                        appointmentInfo.setText(display);

                    } catch (JSONException e) {
                        Log.e("StudentAppointmentFragment", "Error parsing appointment JSON", e);
                    }
                },
                error -> Log.e("StudentAppointmentFragment", "Error fetching appointment", error)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + Authorization.generateAuthToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(get);


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
