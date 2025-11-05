package com.example.myapplication;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AppointmentListFragment extends Fragment {

    private Button makeAppointment;
    private Button updateAppointment;
    private Button deleteAppointment;
    private int studentId;
    private String userEmail;
    private String userPassword;
    private TextView appointmentInfo;
    private EditText editTextAppId;
    private EditText editTextTimeDate;
    private EditText editTextLocation;
    private EditText editTextTitle;

    private int id;

    //private Map<Integer, JSONObject> appointmentHashMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_appointment_list, container, false);

        appointmentInfo = v.findViewById(R.id.appointment_info);
        editTextAppId = v.findViewById(R.id.edit_text_app_id);
        editTextTimeDate = v.findViewById(R.id.edit_text_time_date);
        editTextLocation = v.findViewById(R.id.edit_text_location);
        editTextTitle = v.findViewById(R.id.edit_text_title);
        updateAppointment = v.findViewById(R.id.update_appointment_button);
        deleteAppointment = v.findViewById(R.id.delete_appointment_button);
        makeAppointment = v.findViewById(R.id.make_app_button);

        Bundle bund = getArguments();
        assert bund != null;
        studentId = bund.getInt("userID", -1);
        userEmail = bund.getString("userEmail");
        userPassword = bund.getString("userPassword");
        id = bund.getInt("id", -1);

        Authorization.globalUserEmail = userEmail;
        Authorization.globalPassword = userPassword;

        getAllAppointments();


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


        deleteAppointment.setOnClickListener(view -> {
            String idToUpdate = editTextAppId.getText().toString().trim();
            if(idToUpdate.isEmpty()) {
                Toast.makeText(getContext(), "Enter an appointment id", Toast.LENGTH_SHORT).show();
                return;
            }
            int appId = Integer.parseInt(idToUpdate);

            String deleteURL = "http://coms-3090-066.class.las.iastate.edu:8080/appointments/" + appId;

            StringRequest request = new StringRequest(Request.Method.DELETE, deleteURL,
                    response -> {
                        Toast.makeText(getContext(), "Appointment deleted", Toast.LENGTH_SHORT).show();

                        getAllAppointments();
                    },
                    error -> {
                        Log.e("Appointmentdeletion", "Error deleting an appointment", error);
                        Toast.makeText(getContext(), "Appointment deleted", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Basic " + Authorization.generateAuthToken());
                    return headers;
                }
            };

            VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);

        });


        updateAppointment.setOnClickListener(view -> {
            String idToUpdate = editTextAppId.getText().toString().trim();
            if(idToUpdate.isEmpty()) {
                Toast.makeText(getContext(), "Enter an appointment id", Toast.LENGTH_SHORT).show();
                return;
            }
            int appId = Integer.parseInt(idToUpdate);

            String get_URL = "http://coms-3090-066.class.las.iastate.edu:8080/appointments/" + appId;

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, get_URL, null,
                    get_response -> {
                    try {
                        int id = get_response.getInt("id");
                        String currStartTime = get_response.getString("startTime");
                        String currLocation = get_response.getString("location");
                        String currTitle = get_response.isNull("title") ? "" : get_response.getString("title");
                        int duration = get_response.getInt("duration");
                        int appointmentGroupId = get_response.getInt("appointmentGroupId");
                        String description = get_response.isNull("description") ? "" : get_response.getString("description");
                        String status = get_response.getString("status");


            String updatedTimeDate = editTextTimeDate.getText().toString().trim();
            String updatedLocation = editTextLocation.getText().toString().trim();
            String updatedTitle = editTextTitle.getText().toString().trim();

            JSONObject req = new JSONObject();
                        //req.put("id", id);
                        req.put("startTime", updatedTimeDate.isEmpty() ? currStartTime : updatedTimeDate);
                        req.put("location", updatedLocation.isEmpty() ? currLocation : updatedLocation);
                        req.put("title", updatedTitle.isEmpty() ? currTitle : updatedTitle);
                        req.put("duration", duration);
                        req.put("appointmentGroupId", appointmentGroupId);
                        req.put("description", description);
                       // req.put("status", status);

            String updateURL = "http://coms-3090-066.class.las.iastate.edu:8080/appointments/" + appId;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, updateURL, req,
                    response -> {
                        Toast.makeText(getContext(), "Appointment updated", Toast.LENGTH_SHORT).show();

                        getAllAppointments();
                    },
                    error -> {
                        Log.e("Appointmentupdate", "Error updating an appointment", error);
                        Toast.makeText(getContext(), "Appointment updated", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Basic " + Authorization.generateAuthToken());
                    return headers;
                }
            };

            VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error preparing update", Toast.LENGTH_SHORT).show();
                    }
                    },
                    error -> {
                        Log.e("Appointmentupdate", "Error fetching appointment for update", error);
                        Toast.makeText(getContext(), "Error fetching appointment", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Basic " + Authorization.generateAuthToken());
                    return headers;
                }
            };

            VolleySingleton.getInstance(requireContext()).addToRequestQueue(getRequest);

        });


        return v;
    }

    private void getAllAppointments() {
        String getURL = "http://coms-3090-066.class.las.iastate.edu:8080/appointments";

        JsonArrayRequest get = new JsonArrayRequest(Request.Method.GET, getURL, null,
                response -> {
                    try {
                        StringBuilder appointmentList = new StringBuilder();

                        for(int i = 0; i < response.length(); i++) {
                            JSONObject appointment = response.getJSONObject(i);

                            int id = appointment.getInt("id");
                           // appointmentHashMap.put(id, appointment);
                            String startTime = appointment.getString("startTime");
                            int duration = appointment.getInt("duration");
                            int appointmentGroupId = appointment.getInt("appointmentGroupId");
                            String status = appointment.getString("status");

                            String location = appointment.isNull("location") ? "" : appointment.getString("location");
                            String title = appointment.isNull("title") ? "" : appointment.getString("title");
                            String description = appointment.isNull("description") ? "" : appointment.getString("description");

                            appointmentList.append("Appointment ID: ").append(id)
                                    .append("\nTitle: ").append(title)
                                    .append("\nTime: ").append(startTime)
                                    .append("\nDuration: ").append(duration).append(" min")
                                    .append("\nLocation: ").append(location)
                                    .append("\nStatus: ").append(status)
                                    .append("\nDescription: ").append(description)
                                    .append("\n\n");

                        }


                        appointmentInfo.setText(appointmentList.toString());

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

    }
}
