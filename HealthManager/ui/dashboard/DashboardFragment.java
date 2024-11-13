package com.example.healthmanager.ui.dashboard;

import static com.example.healthmanager.ChooseActivity.selectedExer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthmanager.ChooseActivity;
import com.example.healthmanager.DatabaseHelper;
import com.example.healthmanager.ExerciseDatabaseHelper;
import com.example.healthmanager.R;
import com.example.healthmanager.Record;
import com.example.healthmanager.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    Button btnStart, btnStop, btnPause, btnChoose;
    TextView viewTime, readyExer;
    int sec, min = 0, hour = 0;
    long start_time, end_time, total_time, pause_start, pause_end, pause_time = 0;
    public static boolean isRunning, isPause;
    private FragmentDashboardBinding binding;
    private ActivityResultLauncher<Intent> resultLauncher;
    String showstartTime;
    ArrayList<String> exerciseArray;
    ArrayList<Record> recordArray;
    DatabaseHelper myName;
    ExerciseDatabaseHelper myExercise;
    ListView dashboardListview;
    MyDashboardAdapter adapter;
    ExerciseDatabaseHelper myDb;
    DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myName = new DatabaseHelper(getActivity());
        myExercise = new ExerciseDatabaseHelper(getActivity());
        viewTime = binding.viewTime;
        readyExer = binding.readyexer;
        myDb = new ExerciseDatabaseHelper(getActivity());

        exerciseArray = new ArrayList<String>();
        recordArray = new ArrayList<Record>();

        btnStart = binding.start;
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dashboardViewModel.getRunning()){
                    start_time = SystemClock.elapsedRealtime();
                    dashboardViewModel.setRunning(true);

                    showstartTime = getCurrentTime();
                    viewTime.setText("시작 시간 :" + showstartTime);
                    dashboardViewModel.setTime(start_time);
                    dashboardViewModel.setItem(showstartTime);

                    dashboardViewModel.setPause(false);
                    btnStart.setClickable(false);
                    btnStop.setClickable(true);
                    btnPause.setClickable(true);
                }
                dashboardViewModel.setExercise(selectedExer);
                exerciseArray.clear();

                getExercise(selectedExer);
                readyExer.setText(" " + selectedExer);

                btnStart.setClickable(false);
                adapter = new DashboardFragment.MyDashboardAdapter(getActivity(), exerciseArray);

                dashboardListview = binding.dashListview;
                dashboardListview.setAdapter(adapter);
            }
        });

        btnPause = binding.pause;
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPause = !dashboardViewModel.getPause();
                dashboardViewModel.setPause(isPause);
                if (dashboardViewModel.getPause()) {
                    dashboardViewModel.setRunning(false);
                    pause_start = SystemClock.elapsedRealtime();
                    btnPause.setText("재시작");
                }
                else{
                    pause_end = SystemClock.elapsedRealtime();
                    pause_time += pause_end - pause_start;
                    btnPause.setText("일시 정지");
                    dashboardViewModel.setRunning(true);
                }
            }
        });

        btnStop = binding.stop;
        btnStop.setClickable(false);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("주의!");
                dlg.setIcon(R.drawable.caution);

                View dialogView;

                dialogView = (View) View.inflate(getActivity(), R.layout.btnstop, null);

                dlg.setView(dialogView);

                dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "ok action", Toast.LENGTH_SHORT).show();
                        end_time = SystemClock.elapsedRealtime();
                        dashboardViewModel.setRunning(false);
                        total_time = end_time - dashboardViewModel.getTime() - pause_time;
                        sec = (int) (total_time / 1000);
                        if (sec > 60){
                            min += sec / 60;
                        }
                        if (min > 60){
                            hour += min / 60;
                        }
                        sec = sec % 60;

                        btnStop.setClickable(false);
                        btnPause.setClickable(false);
                        btnStart.setClickable(false);

                        getExercise(ChooseActivity.selectedExer);
                        readyExer.setText(" 운동 준비 중...");
                        viewTime.setText("---");

                        String time = hour + "." + min + "." + sec;

                        AddData(selectedExer, recordArray, getCurrentDate(), time);

                        exerciseArray.clear();
                        recordArray.clear();
                        dashboardViewModel.setExercise("");
                        adapter.notifyDataSetChanged();
                    }
                });
                dlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "cancel action", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        btnChoose = binding.choose;
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setClickable(true);
                dashboardViewModel.setRunning(false);
                chooseClick(v);
            }
        });

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            public void onActivityResult(ActivityResult result) {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dashboardViewModel.getRunning() && !dashboardViewModel.getPause()) {
            btnStart.setClickable(false);
            btnPause.setClickable(true);
            readyExer.setText(" " + dashboardViewModel.getExercise());

            getExercise(dashboardViewModel.getExercise());
            adapter = new DashboardFragment.MyDashboardAdapter(getActivity(), exerciseArray);
            dashboardListview = binding.dashListview;
            if(!dashboardViewModel.getExercise().equals("")){
                dashboardListview.setAdapter(adapter);
            }

            viewTime.setText("시작 시간 :" + dashboardViewModel.getItem());
        } else if (!dashboardViewModel.getPause() && !dashboardViewModel.getRunning()){
            btnStop.setClickable(false);
            btnPause.setClickable(false);

            readyExer = binding.readyexer;
            readyExer.setText(" 운동 준비 중...");
        }
    }

    public void AddData(String name, ArrayList<Record> records, String date, String time) {
        boolean isInserted = myDb.insertData(name, records, date, time);
        if (isInserted)
            Toast.makeText(getActivity(), "저장되었습니다.", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity(), "저장에 실패했습니다.", Toast.LENGTH_LONG).show();
    }



    public void chooseClick(View v){
        Intent intent = new Intent(getActivity(), ChooseActivity.class);

        resultLauncher.launch(intent);
    }

    public void getExercise(String findName) {
        Cursor res = myName.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(getActivity(), "운동이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        while (res.moveToNext()) {
            String name = res.getString(0);
            String exercise = res.getString(1);
            if(exercise != null && name.equals(findName)){
                exerciseArray.add(exercise);
            }
        }

        res.close();
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return formatter.format(calendar.getTime());
    }
    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        return year + "." + month + "." + dayOfMonth;
    }

    class MyDashboardAdapter extends BaseAdapter {

        Context mContext = null;
        ArrayList<String> mData = null;
        ArrayList<Record> mRecordData = new ArrayList<>();

        public MyDashboardAdapter(Context context, ArrayList<String> data) {
            mContext = context;
            mData = data;

            for (int i = 0; i < data.size(); i++) {
                mRecordData.add(new Record("", "", "", ""));
            }
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemLayout = inflater.inflate(R.layout.dashfragment_list, parent, false);

            ImageView imageView = (ImageView) itemLayout.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.folder);

            TextView textView = (TextView) itemLayout.findViewById(R.id.listviewName);
            textView.setText(mData.get(position));

            EditText setsEditText = itemLayout.findViewById(R.id.sets);
            EditText minWeightEditText = itemLayout.findViewById(R.id.minweight);
            EditText maxWeightEditText = itemLayout.findViewById(R.id.maxweight);

            Record currentRecord = mRecordData.get(position);

            setsEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentRecord.setSets(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            minWeightEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentRecord.setMinWeight(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            maxWeightEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentRecord.setMaxWeight(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            CheckBox checkBox = itemLayout.findViewById(R.id.listviewCheck);
            checkBox.setChecked(currentRecord.isChecked());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    currentRecord.setChecked(isChecked);
                    currentRecord.setExercise(mData.get(position));
                    currentRecord.setSets(setsEditText.getText().toString());
                    currentRecord.setMinWeight(minWeightEditText.getText().toString());
                    currentRecord.setMaxWeight(maxWeightEditText.getText().toString());

                    if (isChecked) {
                        if (!recordArray.contains(currentRecord)) {
                            recordArray.add(currentRecord);
                        }
                    } else {
                        recordArray.remove(currentRecord);
                    }
                }
            });

            return itemLayout;
        }
    }
}

