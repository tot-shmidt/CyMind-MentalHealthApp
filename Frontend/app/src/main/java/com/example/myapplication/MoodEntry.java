package com.example.myapplication;

public class MoodEntry {
    private int id;
    private String date;
    private int moodRating;
    private int userId;
    private String journalId;

    public MoodEntry(int id, String date, int moodRating, int userId, String journalId) {
        this.id = id;
        this.date = date;
        this.moodRating = moodRating;
        this.userId = userId;
        this.journalId = journalId;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public int getMoodRating() { return moodRating; }
    public int getUserId() { return userId; }
    public String getJournalId() { return journalId; }
}
