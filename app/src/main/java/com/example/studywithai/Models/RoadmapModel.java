package com.example.studywithai.Models;

public class RoadmapModel {
    private int id;
    private String subjectName;
    private String goal;
    private String status;
    private int progress; // 0 - 100%
    private boolean isPinned;

    public RoadmapModel(int id, String subjectName, String goal, String status, int progress, boolean isPinned) {
        this.id = id;
        this.subjectName = subjectName;
        this.goal = goal;
        this.status = status;
        this.progress = progress;
        this.isPinned = isPinned;
    }

    public int getId() {
        return id;
    }
    public String getSubjectName() {
        return subjectName;
    }
    public String getGoal() {
        return goal;
    }
    public String getStatus() {
        return status;
    }
    public int getProgress() {
        return progress;
    }
    public boolean isPinned() {
        return isPinned;
    }
}