package com.example.healthmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ChooseActivity extends AppCompatActivity{

    Button btnBack;
    ListView manageList;
    ArrayList<String> myArray, showArray;
    public static String selectedExer = "";
    DatabaseHelper myName;
    TextView showText;
    int exerciseIndex;
    MyManageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_action);

        myName = new DatabaseHelper(this);

        View manage_shortclick = (View) View.inflate(ChooseActivity.this, R.layout.manage_shortclick, null);
        showText = manage_shortclick.findViewById(R.id.showdata);

        btnBack = (Button) findViewById(R.id.manage_back);
        manageList = (ListView) findViewById(R.id.choose_listview);
        myArray = new ArrayList<String>();
        showArray = new ArrayList<String>();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        adapter = new MyManageAdapter(this, myArray);

        manageList.setAdapter(adapter);

        manageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                String selectedItem_short = myArray.get(arg2);

                AlertDialog.Builder dlg = new AlertDialog.Builder(ChooseActivity.this);

                View dialogView;
                dialogView = (View) View.inflate(ChooseActivity.this, R.layout.dashboard_choose, null);

                TextView chooseText = (TextView) dialogView.findViewById(R.id.chooseText);
                chooseText.setText("\n" + selectedItem_short + "(으)로 운동을 시작하시겠습니까?");

                dlg.setView(dialogView);

                dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedExer = selectedItem_short;
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

        manageList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selectedItem_short = myArray.get(arg2);
                getData(selectedItem_short);

                return false;
            }
        });

        viewAll();
    }

    public void viewAll() {
        Cursor res = myName.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(ChooseActivity.this, "운동이 없습니다.", Toast.LENGTH_SHORT).show();
            myArray.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        myArray.clear();
        showArray.clear();

        while (res.moveToNext()) {
            String name = res.getString(0);
            String exercise = res.getString(1);

            if (exercise == null || exercise.isEmpty()) {
                myArray.add(name);
            }
        }

        res.close();
        adapter.notifyDataSetChanged();
    }

    public void getData(String findName) {
        Cursor res = myName.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(ChooseActivity.this, "운동이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            String name = res.getString(0);
            String exercise = res.getString(1);
            if(exercise != null && name.equals(findName)){
                buffer.append("운동 : " + exercise + "\n\n");
            }
        }

        showMessage("운동\n", buffer.toString());

        setMessage();

        res.close();
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void setMessage(){
        Cursor res = myName.getAllData();

        if (res.getCount() == 0) {
            showText.setText("운동이 없습니다.");
            return;
        }

        StringBuilder markss = new StringBuilder();
        while (res.moveToNext()) {
            if (res.getColumnIndex(DatabaseHelper.COL_1) != -1){
                exerciseIndex = res.getColumnIndex(DatabaseHelper.COL_2);
            }
            String exercise = res.getString(exerciseIndex);

            markss.append("운동: ").append(exercise).append("\n");
        }

        showText.setText(markss.toString());
    }
}

class MyManageAdapter extends BaseAdapter {

    Context mContext = null;
    ArrayList<String> mData = null;

    public MyManageAdapter(Context context, ArrayList<String> data) {
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
    public String getItem(int position) {
        return mData.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemLayout = inflater.inflate(R.layout.listview_manage, parent, false);

        ImageView imageView = (ImageView) itemLayout.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.folder);

        TextView textView = (TextView) itemLayout.findViewById(R.id.listviewName);
        textView.setText(mData.get(position));

        return itemLayout;
    }
}