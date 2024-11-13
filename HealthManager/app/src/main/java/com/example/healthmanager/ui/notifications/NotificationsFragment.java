package com.example.healthmanager.ui.notifications;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthmanager.ExerciseDatabaseHelper;
import com.example.healthmanager.ManageActivity;
import com.example.healthmanager.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class NotificationsFragment extends Fragment {

    ExerciseDatabaseHelper myDb;
    TextView setData;
    DatePicker datePicker;
    Button reset;
    SQLiteDatabase sqlDB;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myDb = new ExerciseDatabaseHelper(getActivity());
        datePicker = binding.datePicker;

        reset = binding.reset;
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllData();
            }
        });

        setData = binding.setdata;

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "." + (monthOfYear + 1) + "." + dayOfMonth;
                getData(date);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void deleteAllData() {
        sqlDB = myDb.getWritableDatabase();
        myDb.onUpgrade(sqlDB, 1, 2);
        sqlDB.close();
    }

    public void getData(String data) {
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            setData.setText("운동을 하지 않았습니다.");
            return;
        }

        ArrayList<String> arr = new ArrayList<String>();
        String name = "";
        String date = "";

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            name = res.getString(0);
            String exercise = res.getString(1);
            String sets = res.getString(2);
            String weight = res.getString(3);
            date = res.getString(4);
            String time = res.getString(5);
            String[] timeParts = time.split("\\.");
            if (data.equals(date)) {
                buffer.append(name + "\n운동 : " + exercise + "\n세트 : " + sets + "\n무게 : " + weight + "\n\n");

                String exerciseTime = name + " 운동의 시간 :\n" + timeParts[0] + "시간 " + timeParts[1] + "분 " + timeParts[2] + "초\n\n";
                if (!arr.contains(exerciseTime)) {
                    arr.add(exerciseTime);
                }
            }
        }

        for(String str : arr){
            buffer.append(str);
        }

        setData.setText(buffer);

        res.close();
    }
}