package com.example.myapplication;

import static android.widget.Toast.makeText;
import static com.example.myapplication.Authorization.generateAuthToken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAppointmentFragment extends Fragment {

    private Spinner dateSpinner;
    private Spinner startTimeSpinner;

    private Spinner durationSpinner;
    private Spinner locSpinner;
    private Spinner profSpinner;

    private EditText title;
    private EditText description;

    private Button submitButton;
    private int studentId;
    private String userEmail;
    private String userPassword;

private  List<Integer> professionalIdList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_student_appointment, container, false);

        Bundle bund = getArguments();
        assert bund != null;
        studentId = bund.getInt("userID", -1);
        userEmail = bund.getString("userEmail");
        userPassword = bund.getString("userPassword");

        /* initialize UI elements */

        dateSpinner = v.findViewById(R.id.date_spinner);
        startTimeSpinner = v.findViewById(R.id.startTime_spinner);
        durationSpinner = v.findViewById(R.id.duration_spinner);
        locSpinner = v.findViewById(R.id.loc_spinner);
        profSpinner = v.findViewById(R.id.prof_spinner);
        title = v.findViewById(R.id.title_edit_text);
        description = v.findViewById(R.id.description_edit_text);
        submitButton = v.findViewById(R.id.submit_btn);


        String[] date = {"2025-11-07", "2025-11-08", "2025-11-09", "2025-11-10", "2025-11-11", "2025-11-12", "2025-11-13"};
        String[] time = {"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00"};
        Integer[] duration = {15, 30, 60, 90};
        String[] loc = {"120 Ash Ave", "453 Walnut St", "387 Knapp St", "400 Stanton Ave", "1738 Lincoln Way"};
        //String[] prof = {"Dr.Smith", "Dr.Murphy", "Dr.Jackson", "Dr.Washington", "Dr.e"};

        ArrayAdapter<String> adapterDate;
        adapterDate = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, date);
        adapterDate.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        dateSpinner.setAdapter(adapterDate);

        ArrayAdapter<String> adapterTime;
        adapterTime = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, time);
        adapterTime.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        startTimeSpinner.setAdapter(adapterTime);

        ArrayAdapter<Integer> adapterDuration;
        adapterDuration = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, duration);
        adapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(adapterDuration);

        ArrayAdapter<String> adapterLoc;
        adapterLoc = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, loc);
        adapterLoc.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        locSpinner.setAdapter(adapterLoc);



        getAllProfessionals();

        /* click listener on signup button pressed */
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentId == -1) {
                    Toast.makeText(requireContext(), "student id not passed", Toast.LENGTH_SHORT).show();
                    return;
                }

                Authorization.globalUserEmail = userEmail;
                Authorization.globalPassword = userPassword;

                /* grab strings from user inputs */
                String userDate = dateSpinner.getSelectedItem().toString();
                String userStartTime = startTimeSpinner.getSelectedItem().toString();
                Integer userDuration = (Integer) durationSpinner.getSelectedItem();
                String userLoc = locSpinner.getSelectedItem().toString();
                String userProf = profSpinner.getSelectedItem().toString();
                String userTitle = title.getText().toString();
                String userDescription = description.getText().toString();

                JSONObject request = new JSONObject();
                //try to pass the credentials into designated parameters
                try {
                    request.put("studentId", studentId);
                    //request.put("professionalIds", userProf);

                    int selectedIndex = profSpinner.getSelectedItemPosition();
                    int selectedProfId = professionalIdList.get(selectedIndex);
                    JSONArray professionalIds = new JSONArray();
                    professionalIds.put(selectedProfId);
                    request.put("professionalIds", professionalIds);

                    request.put("groupName", userTitle.isEmpty() ? "Appointment Group" : userTitle);
                }
                //catch it if errors occur when requesting
                catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                //connect to server with sign up POST endpoint (created from backend)
                String postUrl = "http://coms-3090-066.class.las.iastate.edu:8080/appointments/groups";

                //request to post info as json
                JsonObjectRequest post = new JsonObjectRequest(Request.Method.POST, postUrl, request,
                        response -> {
                            //get response body values from backend
                            try {
                                int id = response.getInt("id");

                                bookAppointment(id);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        //Ensure that the server and app communicate and send/retreive info properly, if not display error message
                        error -> Toast.makeText(requireContext(), "Request failed", Toast.LENGTH_LONG).show()

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "Basic " + generateAuthToken());
                        return headers;
                    }
                };
                //finally, add the post to queue
                VolleySingleton.getInstance(requireContext()).addToRequestQueue(post);


                //StudentAppointmentSubmitFragment fragment = new StudentAppointmentSubmitFragment();
                //Bundle args = new Bundle();

            }
        });
        return v;
    }

    private void getAllProfessionals() {
        String getURL =  "http://coms-3090-066.class.las.iastate.edu:8080/users/professional";

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(
                Request.Method.GET, // HTTP method
                getURL,
                null, // Request body (null for GET request)
                response -> {
                    // Log response for debugging
                    Log.d("Volley Response", response.toString());
                    List<String> professionalList = new ArrayList<>();
                    // Parse JSONArray by sending it to MoodEntry to become a JSONObject
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String profFullName = obj.getString("firstName") + " " + obj.getString("lastName");
                            int profId = obj.getInt("userId");
                            professionalList.add(profFullName);
                            professionalIdList.add(profId);
                        }
                    } catch (JSONException e) {
                        Log.e("professional list", "Failed to parse JSON response");
                    }

                    ArrayAdapter<String> adapterProf;
                    adapterProf = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, professionalList);
                    adapterProf.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                    profSpinner.setAdapter(adapterProf);

                },
                error -> {
                    // Log error details
                    Log.e("Volley Error (getProfessionalList)", error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Define headers if needed
                HashMap<String, String> headers = new HashMap<>();
                // Headers
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                // Define parameters if needed
                // Example parameter
                // params.put("param1", "value1");
                return new HashMap<>();
            }
        };

        // Adding request to the Volley request queue
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjReq);
    }

    private void bookAppointment(int appointmentGroupId) {
        String userStartTime = startTimeSpinner.getSelectedItem().toString();
        Integer userDuration = (Integer) durationSpinner.getSelectedItem();
        String userLoc = locSpinner.getSelectedItem().toString();
        String userTitle = title.getText().toString();
        String userDescription = description.getText().toString();

        String userDate = dateSpinner.getSelectedItem().toString();
        String dateTime = userDate + "T" + userStartTime + ":00";

        JSONObject request = new JSONObject();
        try {
            request.put("startTime", dateTime);
            request.put("duration", userDuration);
            request.put("appointmentGroupId", appointmentGroupId);
            request.put("location", userLoc);
            request.put("title", userTitle.isEmpty() ? "Appointment" : userTitle);
            request.put("description", userDescription.isEmpty() ? "" : userDescription);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String postUrl = "http://coms-3090-066.class.las.iastate.edu:8080/appointments";

        JsonObjectRequest post = new JsonObjectRequest(Request.Method.POST, postUrl, request,
                response -> {
                    try {
                        int id = response.getInt("id");
                        String status = response.getString("status");
                        Toast.makeText(requireContext(),
                                "Appointment booked (ID: " + id + ", Status: " + status + ")", Toast.LENGTH_LONG).show();
                        Log.d("StudentAppointment", "Booked appointment ID: " + id + " Status: " + status);



                        AppointmentListFragment appList = new AppointmentListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", id);
                        bundle.putString("userEmail", userEmail);
                        bundle.putString("userPassword", userPassword);
                        appList.setArguments(bundle);

                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, appList).commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), "Appointment booking failed", Toast.LENGTH_LONG).show();
                    Log.e("StudentAppointment", "Error booking appointment: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Define headers if needed
                HashMap<String, String> headers = new HashMap<>();
                // Headers
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(post);
    }




}




