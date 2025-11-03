package com.example.myapplication;

public class Resource {
    private final String title;
    private final int authorId;
    private final String author;
    private final String categories;
    private final String description;

    public Resource(String title, int authorId, String author, String categories, String description) {
        this.title = title;
        this.authorId = authorId;
        this.author = author;
        this.categories = categories;
        this.description = description;
    }

    public String getTitle() { return title; }
    public int getAuthorId() { return authorId; }
    public String getAuthor() { return author; }
    public String getCategories() { return categories; }
    public String getDescription() { return description; }
}
