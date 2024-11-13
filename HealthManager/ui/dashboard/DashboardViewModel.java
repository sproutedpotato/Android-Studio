package com.example.healthmanager.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private String startTime;
    private long start_time = 0;
    private String selectedExercise;
    private boolean isRunning = false, isPause = false;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(String text) {
        mText.setValue(text);
    }
    public void setItem(String text){
        this.startTime = text;
    }
    public String getItem(){
        return startTime;
    }
    public void setTime(long time){
        this.start_time = time;
    }
    public long getTime(){
        return start_time;
    }
    public void setExercise(String text){
        this.selectedExercise = text;
    }
    public String getExercise(){
        return selectedExercise;
    }
    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }
    public boolean getRunning(){
        return isRunning;
    }
    public void setPause(boolean isPause){
        this.isPause = isPause;
    }
    public boolean getPause(){
        return this.isPause;
    }
}