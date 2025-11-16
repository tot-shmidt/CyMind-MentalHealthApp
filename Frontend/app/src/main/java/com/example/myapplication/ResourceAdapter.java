package com.example.myapplication;

import static com.example.myapplication.Authorization.generateAuthToken;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {
    private static final String TAG = "ResourceAdapter";
    private static final String APP_API_URL = "http://coms-3090-066.class.las.iastate.edu:8080/";
    private static final String POSTMAN_API_URL = "https://834f7701-6129-40fc-b41d-30cf356d46b0.mock.pstmn.io/";

    private List<Resource> resourceList;
    private Context context;
    private boolean isProfessional;
    private OnResourceClickListener listener;

    public interface OnResourceClickListener {
        void onResourceClick(Resource resource, int position);
    }
    public ResourceAdapter(List<Resource> resourceList, Context context, boolean isProfessional, OnResourceClickListener listener) {
        this.resourceList = resourceList;
        this.context = context;
        this.isProfessional = isProfessional;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, preview, categories;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.resourceTitle);
            author = view.findViewById(R.id.resourceAuthor);
            preview = view.findViewById(R.id.resourcePreview);
            categories = view.findViewById(R.id.resourceCategories);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resource_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Resource resource = resourceList.get(position);

        holder.title.setText(resource.getTitle() != null ? resource.getTitle() : "");

        // Format multiple authors
        String authorsText = "";
        if (resource.getAuthors() != null && !resource.getAuthors().isEmpty()) {
            authorsText = String.join(", ", resource.getAuthors());
        }
        holder.author.setText(authorsText);

        holder.categories.setText(resource.getCategories() != null ? resource.getCategories() : "");
        holder.preview.setText(resource.getDescription() != null ? resource.getDescription() : "");


        holder.itemView.setOnClickListener(v -> {
            if (isProfessional) {
                // Professional behavior: open update/delete dialog
                listener.onResourceClick(resource, position);
            } else {
                // Student behavior: open info dialog
                showResourceDialog(resource);
            }
        });
    }

    // Public method to allow ProfessionalResourceFragment to show dialog
    public void showResourceDialogPublic(Resource resource) {
        Log.d(TAG, "showResourceDialogPublic called for resource: " + resource.getTitle() + " (ID: " + resource.getId() + ")");
        showResourceDialog(resource);
    }

    void showResourceDialog(Resource resource) {
        Log.d(TAG, "showResourceDialog starting for resource ID: " + resource.getId());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.resource_dialog, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.dialogTitle);
        TextView author = dialogView.findViewById(R.id.dialogAuthor);
        TextView categories = dialogView.findViewById(R.id.dialogCategories);
        TextView description = dialogView.findViewById(R.id.dialogDescription);
        LinearLayout exercisesContainer = dialogView.findViewById(R.id.exercisesContainer);
        TextView exercisesHeader = dialogView.findViewById(R.id.exercisesHeader);

        Log.d(TAG, "Dialog views initialized, exercisesContainer: " + (exercisesContainer != null ? "found" : "NULL") +
                   ", exercisesHeader: " + (exercisesHeader != null ? "found" : "NULL"));

        title.setText(resource.getTitle());

        // Format multiple authors
        String authorsText = "By ";
        if (resource.getAuthors() != null && !resource.getAuthors().isEmpty()) {
            authorsText += String.join(", ", resource.getAuthors());
        } else {
            authorsText += "Unknown";
        }
        author.setText(authorsText);

        categories.setText(resource.getCategories());
        description.setText(resource.getDescription());

        // Hide exercises section initially
        exercisesHeader.setVisibility(View.GONE);
        exercisesContainer.setVisibility(View.GONE);

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

        // Fetch exercises for this resource
        fetchExercises(resource.getId(), exercises -> {
            Log.d(TAG, "Exercise callback received. Exercises: " + (exercises != null ? exercises.size() + " items" : "NULL"));

            // Show exercises section
            exercisesHeader.setVisibility(View.VISIBLE);
            exercisesContainer.setVisibility(View.VISIBLE);

            if (exercises != null && !exercises.isEmpty()) {
                Log.d(TAG, "Displaying " + exercises.size() + " exercises");
                // Add each exercise to the container
                for (Exercise exercise : exercises) {
                    View exerciseView = LayoutInflater.from(context).inflate(R.layout.exercise_item_layout, exercisesContainer, false);

                    TextView exerciseName = exerciseView.findViewById(R.id.exerciseName);
                    TextView exerciseType = exerciseView.findViewById(R.id.exerciseType);
                    TextView exerciseContent = exerciseView.findViewById(R.id.exerciseContent);

                    exerciseName.setText(exercise.getExerciseName());
                    exerciseType.setText(exercise.getExerciseType());
                    exerciseContent.setText(exercise.getContent());

                    exercisesContainer.addView(exerciseView);
                }
            } else {
                Log.d(TAG, "No exercises to display, showing 'No exercises related' message");
                // Show "No exercises related" message
                TextView noExercisesText = new TextView(context);
                noExercisesText.setText("No exercises related");
                noExercisesText.setTextSize(14);
                noExercisesText.setPadding(8, 8, 8, 8);
                exercisesContainer.addView(noExercisesText);
            }
        });
    }

    private void fetchExercises(int articleId, ExerciseCallback callback) {
        String url = POSTMAN_API_URL + "resources/articles/" + articleId + "/exercises";

        Log.d(TAG, "Fetching exercises for article ID: " + articleId);
        Log.d(TAG, "Exercise URL: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                Log.d(TAG, "Fetched " + response.length() + " exercises");
                List<Exercise> exercises = new ArrayList<>();

                try {
                    // Parse exercise objects: [{id, exerciseName, content, exerciseType}, ...]
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject exerciseObj = response.getJSONObject(i);

                        int id = exerciseObj.getInt("id");
                        String exerciseName = exerciseObj.getString("exerciseName");
                        String content = exerciseObj.getString("content");
                        String exerciseType = exerciseObj.getString("exerciseType");

                        Exercise exercise = new Exercise(id, exerciseName, content, exerciseType);
                        exercises.add(exercise);
                    }

                    Log.d(TAG, "Successfully parsed " + exercises.size() + " exercises");
                    callback.onSuccess(exercises);

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing exercises", e);
                    callback.onSuccess(null);
                }
            },
            error -> {
                Log.e(TAG, "Error fetching exercises: " + error.toString());
                if (error.networkResponse != null) {
                    Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                    Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                } else {
                    Log.e(TAG, "Network Response is null");
                }
                callback.onSuccess(null);
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

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    // Callback interface for exercise fetch operations
    private interface ExerciseCallback {
        void onSuccess(List<Exercise> exercises);
    }

    @Override
    public int getItemCount() {
        return resourceList.size();
    }
}
