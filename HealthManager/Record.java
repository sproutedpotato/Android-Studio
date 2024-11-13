package com.example.healthmanager;

public class Record {
    private String exercise;
    private String sets;
    private String minWeight;
    private String maxWeight;
    private boolean checked;

    public Record(String exercise, String sets, String minWeight, String maxWeight) {
        this.exercise = exercise;
        this.sets = sets;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.checked = false;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(String minWeight) {
        this.minWeight = minWeight;
    }

    public String getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(String maxWeight) {
        this.maxWeight = maxWeight;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
