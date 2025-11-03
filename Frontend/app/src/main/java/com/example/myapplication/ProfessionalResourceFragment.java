package com.example.myapplication;

import static com.example.myapplication.Authorization.generateAuthToken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfessionalResourceFragment extends Fragment {

    private int userID;
    private String userFirstName;
    private String userLastName;
    private String userEmail;

    private List<Resource> resources = new ArrayList<>();
    private ResourceAdapter resourceAdapter;
    private Button createButton;
    private static final String APP_API_URL = "https://834f7701-6129-40fc-b41d-30cf356d46b0.mock.pstmn.io/";

    // TODO: add view by category
    // TODO: swap to professional specific details

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_professional_resource, container, false);

        RecyclerView resourceViewer = rootView.findViewById(R.id.resourceViewer);
        resourceViewer.setLayoutManager(new LinearLayoutManager(getContext()));

        this.resourceAdapter = new ResourceAdapter(resources, getContext(), true, this::showResourceOptionsDialog);
        resourceViewer.setAdapter(this.resourceAdapter); // Use the field here as well

        createButton = rootView.findViewById(R.id.createButton);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get the passed user info from previous page
        userEmail = getActivity().getIntent().getStringExtra("userEmail");
        userID = getActivity().getIntent().getIntExtra("userID", 0);
        userFirstName = getActivity().getIntent().getStringExtra("userFirstName");
        userLastName = getActivity().getIntent().getStringExtra("userLastName");

//        userMajor = getActivity().getIntent().getStringExtra("userMajor");
//        userYearOfStudy = getActivity().getIntent().getIntExtra("userYearOfStudy", 0);

        createButton.setOnClickListener(v-> {
            Intent intentReturn = new Intent(getActivity(), CreateResourceActivity.class);
            startActivity(intentReturn);
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Clear old data
        clear();

        // Re-fetch fresh data
        getArticlesByCategory("all");
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
                    // Loop through each element in the JSONArray
                    for (int i = 0; i < response.length(); i++) {
                        int articleId = response.getInt(i);


                        // Fetch the full resource for each ID
                        getStudentResource(articleId);
                        Log.d("Volley Test", "created resource for article ID + articleId");

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
                        int id = response.getInt("id");
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

                        Resource resource = new Resource(id, title, authorId, author, categories, description);
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

    private void showResourceOptionsDialog(Resource resource, int position) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle(resource.getTitle())
            .setMessage("Select an action for this resource.")
            .setPositiveButton("Update", (dialog, which) -> {
                //Use intent to go to the next page, in this case the home page
                Intent intent = new Intent(getActivity(), UpdateResourceActivity.class);

                //send user email and pass and other vals to homepage
                intent.putExtra("resourceId", resource.getId());
                intent.putExtra("resourceAuthorId", resource.getAuthorId());
                intent.putExtra("resourceAuthor", resource.getAuthor());
                intent.putExtra("resourceTitle", resource.getTitle());
                intent.putExtra("resourceCategories", resource.getCategories());
                intent.putExtra("resourceContent", resource.getDescription());

                startActivity(intent);
            })
            .setNegativeButton("Delete", (dialog, which) -> {
                deleteResource(resource, position);
            })
            .setNeutralButton("Close", null)
            .show();
    }

    private void deleteResource(Resource resource, int position) {
        // Creates new request defined as a DELETE request
        // Use StringRequest since there is no json response body, just status code
        String deleteURL = APP_API_URL + "resources/articles?id=" + resource.getId();
        StringRequest delete = new StringRequest(Request.Method.DELETE, deleteURL,
            response -> {
                resources.remove(position);
                resourceAdapter.notifyItemRemoved(position);
                //Display message saying user was deleted by identifying their id
                Toast.makeText(getActivity(), "Resource with an id: " + userID + " was successfully deleted", Toast.LENGTH_LONG).show();
                // Clear old data
                if (resourceAdapter.getItemCount() > 0) {
                    clear();
                }
                // Re-fetch fresh data
                getArticlesByCategory("all");
            },
            error -> {
                //display error message if one occurs
                Toast.makeText(getActivity(), "Error deleting resource", Toast.LENGTH_LONG).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + Authorization.generateAuthToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        //finally, if no issues, add the delete to queue
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(delete);
    }

    private void clear() {
        int size = resources.size();
        resources.clear();
        resourceAdapter.notifyItemRangeRemoved(0, size);
    }
}