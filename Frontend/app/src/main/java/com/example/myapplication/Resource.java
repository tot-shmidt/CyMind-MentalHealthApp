package com.example.myapplication;

import static com.example.myapplication.Authorization.generateAuthToken;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Resource {
    private final String title;
    private final int authorId;
    private final String categories;
    private final String description;

    public Resource(String title, int author, String categories, String description) {
        this.title = title;
        this.authorId = author;
        this.categories = categories;
        this.description = description;
    }

    public String getTitle() { return title; }
    public int getAuthorId() { return authorId; }
    public String getCategories() { return categories; }
    public String getDescription() { return description; }
}
