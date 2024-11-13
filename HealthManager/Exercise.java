package com.example.healthmanager;

public class Exercise {
    public String name;
    public boolean isChecked;

    public Exercise(String str) {
        name = str;
        isChecked = false;
    }

    public String getName() {
        return name;
    }
}
