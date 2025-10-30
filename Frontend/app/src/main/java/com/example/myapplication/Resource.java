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
    private final int id;
    private final String title;
    private final int authorId;
    private final String author;
    private final String categories;
    private final String description;

    // TODO make author a list
    public Resource(int id, String title, int authorId, String author, String categories, String description) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.author = author;
        this.categories = categories;
        this.description = description;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getAuthorId() { return authorId; }
    public String getAuthor() { return author; }
    public String getCategories() { return categories; }
    public String getDescription() { return description; }
}
