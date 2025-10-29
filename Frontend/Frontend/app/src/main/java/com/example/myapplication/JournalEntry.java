package com.example.myapplication;

public class JournalEntry {
    private int id;
    private String date;
    private String entryName;
    private String content;
    private int moodId;

    public JournalEntry(int id, String date, String entryName, String content, int moodId) {
        this.id = id;
        this.date = date;
        this.entryName = entryName;
        this.content = content;
        this.moodId = moodId;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getEntryName() { return entryName; }
    public String getContent() { return content; }
    public int getMoodId() { return moodId; }
}
