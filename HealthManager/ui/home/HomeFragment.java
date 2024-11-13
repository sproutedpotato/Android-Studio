package com.example.healthmanager.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthmanager.AddActivity;
import com.example.healthmanager.ExerciseDatabaseHelper;
import com.example.healthmanager.ManageActivity;
import com.example.healthmanager.ManageNormal;
import com.example.healthmanager.R;
import com.example.healthmanager.databinding.FragmentHomeBinding;
import com.example.healthmanager.ui.dashboard.DashboardFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment { //<a href="https://kr.freepik.com/author/freepik/icons/special-lineal_7">Freepik 제작 아이콘</a>

    Button btnAdd, btnManage;
    int totalDays;
    TextView todayExercisetime, todayData, totalDay;
    ExerciseDatabaseHelper myDb;
    private ActivityResultLauncher<Intent> resultLauncher;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        myDb = new ExerciseDatabaseHelper(getActivity());
        todayExercisetime = binding.todayExercise;
        setTime();
        todayData = binding.todayData;
        totalDay = binding.totalDay;

        totalDays = myDb.getDatesCount();
        totalDay.setText("D + " + totalDays);

        btnAdd = binding.addexercise;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

                dlg.setTitle("생성할 운동 목록을 정해주세요");
                dlg.setIcon(R.drawable.caution);

                View dialogView;

                dialogView = (View) View.inflate(getActivity(), R.layout.home_addmanage, null);
                ImageView healthImage = dialogView.findViewById(R.id.home_addmanage_health);
                ImageView normalImage = dialogView.findViewById(R.id.home_addmanage_normal);

                dlg.setView(dialogView);

                AlertDialog dialog = dlg.create();

                healthImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addHealthClick(v);
                        dialog.dismiss();
                    }
                });

                normalImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addNormalClick(v);
                        dialog.dismiss();
                    }
                });

                dialog.show();


            }
        });

        btnManage = binding.manageexercise;
        btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manageClick(v);
            }
        });


        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            public void onActivityResult(ActivityResult result) {

            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        getData(getCurrentDate());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void addHealthClick(View v){
        Intent intent = new Intent(getActivity(), AddActivity.class);

        resultLauncher.launch(intent);
    }

    public void addNormalClick(View v){
        Intent intent = new Intent(getActivity(), ManageNormal.class);

        resultLauncher.launch(intent);
    }

    public void manageClick(View v){
        Intent intent = new Intent(getActivity(), ManageActivity.class);

        resultLauncher.launch(intent);
    }

    public void getData(String data) {
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            todayData.setText("운동을 하지 않았습니다.");
            res.close();
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

        todayData.setText(buffer);

        res.close();
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        return year + "." + month + "." + dayOfMonth;
    }

    public void setTime(){
        String time = myDb.getLatestTime();
        String[] timesPart = time.split("\\.");

        if(timesPart.length > 2){
            todayExercisetime.setText(timesPart[0] + "시간 " + timesPart[1] + "분 " + timesPart[2] + "초");
        }
        else{
            todayExercisetime.setText(time);
        }
    }
}
