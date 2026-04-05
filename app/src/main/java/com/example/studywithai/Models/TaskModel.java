package com.example.studywithai.Models;

public class TaskModel {
    private String taskName;
    private int xpReward;
    private boolean isCompleted;
    private int targetTab;

    public TaskModel(String taskName, int xpReward, boolean isCompleted, int targetTab) {
        this.taskName = taskName;
        this.xpReward = xpReward;
        this.isCompleted = isCompleted;
        this.targetTab = targetTab;
    }

    public String getTaskName() { return taskName; }
    public int getXpReward() { return xpReward; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public int getTargetTab() { return targetTab; }
}