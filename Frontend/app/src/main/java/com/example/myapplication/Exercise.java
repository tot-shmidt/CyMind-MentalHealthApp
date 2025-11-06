package com.example.myapplication;

public class Exercise {
    private int id;
    private String exerciseName;
    private String content;
    private String exerciseType;

    public Exercise(int id, String exerciseName, String content, String exerciseType) {
        this.id = id;
        this.exerciseName = exerciseName;
        this.content = content;
        this.exerciseType = exerciseType;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getContent() {
        return content;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }
}
