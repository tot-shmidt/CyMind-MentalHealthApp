package com.example.myapplication;

import static android.widget.Toast.makeText;
import static com.example.myapplication.Authorization.generateAuthToken;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentResourceFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private int userAge;
    private String userMajor;
    private int userYearOfStudy;
    private List<Resource> resources = new ArrayList<>();
    private ResourceAdapter resourceAdapter;
    private static final String APP_API_URL = "https://834f7701-6129-40fc-b41d-30cf356d46b0.mock.pstmn.io/";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_student_resource, container, false);

        RecyclerView resourceViewer = rootView.findViewById(R.id.resourceViewer);
        resourceViewer.setLayoutManager(new LinearLayoutManager(getContext()));
        resourceAdapter = new ResourceAdapter(resources, getContext());
        resourceViewer.setAdapter(resourceAdapter);

        // Start fetching resources
        getArticlesByCategory("all");

        Spinner categoryDropdown = (Spinner) rootView.findViewById(R.id.categoryDropdown);
        categoryDropdown.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this.getContext(),
                R.array.resource_categories,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        categoryDropdown.setAdapter(adapter);

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
        userMajor = getActivity().getIntent().getStringExtra("userMajor");
        userYearOfStudy = getActivity().getIntent().getIntExtra("userYearOfStudy", 0);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        clear();
        switch (pos) {
            case 0:
                getArticlesByCategory("ALL");
                break;
            case 1:
                getArticlesByCategory("SLEEP");
                break;
            case 2:
                getArticlesByCategory("FOCUS");
                break;
            case 3:
                getArticlesByCategory("SELF_ESTEEM");
                break;
            case 4:
                getArticlesByCategory("ANXIETY");
                break;
            case 5:
                getArticlesByCategory("POSITIVITY");
                break;
            case 6:
                getArticlesByCategory("AWARENESS");
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }

    private void getArticlesByCategory(String category) {
        Log.d("Volley Test", "starting getArticlesByCategory()");
        String url = APP_API_URL + "resources/articles?category=" + category;
        Log.d("Volley Response", "3");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null, // GET request has no body
            response -> {
                Log.d("Volley Response", response.toString());
                try {
                    Log.d("Volley Response", String.valueOf(response.length()));
                    // Loop through each element in the JSONArray
                    for (int i = 0; i < response.length(); i++) {
                        int articleId = response.getInt(i);

                        // Fetch the full resource for each ID
                        getStudentResource(articleId);
                        Log.d("Volley", "created resource for article ID + articleId");
                    }
                } catch (JSONException e) {
                    Log.e("getArticlesByCategory", "JSON parse error", e);
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Failed to parse article IDs for category " + category,
                            Toast.LENGTH_LONG
                    ).show();
                }
            },
            error -> {
                Log.e("Volley Error (getArticlesByCategory)", error.toString());
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Failed to retrieve article IDs for category " + category,
                        Toast.LENGTH_LONG
                ).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };
        VolleySingleton.getInstance(getActivity().getApplicationContext())
                .addToRequestQueue(jsonArrayRequest);
    }

    private void getStudentResource(int articleId) {
        String url = APP_API_URL + "resources/articles?id=" + articleId;
        Log.d("Volley Test", "Starting getStudentResource for ID " + articleId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, // GET request has no body
                response -> {
                    Log.d("Volley Response", response.toString());

                    try {
                        // Parse the single resource object
                        String title = response.getString("articleName");
                        int authorId = response.getInt("authorId");
                        String author = response.getString("author");
                        String category1 = response.optString("category1", "");
                        String category2 = response.optString("category2", "");
                        String category3 = response.optString("category3", "");
                        String description = response.getString("content");

                        String categories = category1;
                        if (!category2.equals("null")) categories += ", " + category2;
                        if (!category3.equals("null")) categories += ", " + category3;

                        Resource resource = new Resource(title, authorId, author, categories, description);
                        resources.add(resource);
                        resourceAdapter.notifyItemInserted(resources.size() - 1); // Updates RecyclerView

                        Log.d("Volley: individual resource", resource.toString());
                    } catch (JSONException e) {
                        Log.e("getStudentResource", "JSON parse error", e);
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Failed to parse resource data for article ID " + articleId,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },
                error -> {
                    Log.e("Volley Error (getStudentResource)", error.toString());
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Failed to retrieve resource for article ID " + articleId,
                            Toast.LENGTH_LONG
                    ).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }
        };

        // Add request to the Volley queue
        VolleySingleton.getInstance(getActivity().getApplicationContext())
                .addToRequestQueue(jsonObjReq);
    }

    private void clear() {
        int size = resources.size();
        resources.clear();
        resourceAdapter.notifyItemRangeRemoved(0, size);
    }
}