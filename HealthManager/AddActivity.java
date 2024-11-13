package com.example.healthmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AddActivity extends AppCompatActivity{

    Button btnBack, btnOk;
    ListView addListview;
    ArrayList<Exercise> data, selectedExercises;
    ArrayList<String> datas;
    DatabaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) { //<a href="https://www.flaticon.com/kr/free-icons/" title="운동 아이콘">운동 아이콘 제작자: Freepik - Flaticon</a>
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_action);

        myDb = new DatabaseHelper(this);

        selectedExercises = new ArrayList<Exercise>();
        data = new ArrayList<Exercise>();
        readCSV();
        //data.add(new Exercise("데드리프트"));
        //data.add(new Exercise("바벨 스쿼트"));
        //data.add(new Exercise("바벨 벤치 프레스"));

        MyAddAdapter adapter = new MyAddAdapter(this, data);

        addListview = (ListView) findViewById(R.id.add_listview);
        addListview.setAdapter(adapter);

        addListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }
        });

        btnBack = (Button) findViewById(R.id.add_back);
        btnOk = (Button) findViewById(R.id.add_ok);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Exercise exercise : data) {
                    if (exercise.isChecked) {
                        selectedExercises.add(exercise);
                    }
                }
                AlertDialog.Builder dlg = new AlertDialog.Builder(AddActivity.this);

                View dialogView;

                dialogView = (View) View.inflate(AddActivity.this, R.layout.adddata, null);
                EditText addEdit = dialogView.findViewById(R.id.addData_name);

                dlg.setView(dialogView);

                dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AddData(addEdit, selectedExercises);
                        AddTitle(addEdit);
                        finish();
                    }
                });
                dlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg.show();
            }
        });

    }
    public void AddData(EditText editName, ArrayList<Exercise> editExer) {
        boolean isInserted = myDb.insertData(editName.getText().toString(), editExer);
        if (isInserted)
            Toast.makeText(this, "생성 완료", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "생성 실패, 다시 시도하세요", Toast.LENGTH_SHORT).show();
    }

    public void AddTitle(EditText editName) {
        boolean isInserted = myDb.insertName(editName.getText().toString());
        if (isInserted)
            Toast.makeText(this, "생성 완료", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "생성 실패, 다시 시도하세요", Toast.LENGTH_SHORT).show();

    }

    public void readCSV() {
        InputStream inputStream = getResources().openRawResource(R.raw.fitness);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        try {
            String line;
            line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                data.add(new Exercise(line));
            }
            reader.close();
        } catch (Exception e) {}
    }
}

class MyAddAdapter extends BaseAdapter {

    Context mContext = null;
    ArrayList<Exercise> mData = null;

    public MyAddAdapter(Context context, ArrayList<Exercise> data) {
        mContext = context;
        mData = data;
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
    public Exercise getItem(int position) {
        return mData.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemLayout = inflater.inflate(R.layout.listview_item, parent, false);

        ImageView imageView = (ImageView) itemLayout.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.fitness);

        TextView textView = (TextView) itemLayout.findViewById(R.id.listviewName);
        textView.setText(mData.get(position).name);

        CheckBox checkBox = itemLayout.findViewById(R.id.listviewCheck);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mData.get(position).isChecked = isChecked;
            }
        });

        checkBox.setChecked(mData.get(position).isChecked);

        return itemLayout;
    }
}
