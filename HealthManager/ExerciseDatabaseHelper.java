package com.example.healthmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ExerciseDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "recordExercise.db";
    public static final String TABLE_NAME = "exercise_record_table";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "EXERCISE";
    public static final String COL_3 = "SETS";
    public static final String COL_4 = "WEIGHT";
    public static final String COL_5 = "DATE";
    public static final String COL_6 = "TIME";

    public ExerciseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (NAME TEXT,EXERCISE TEXT,SETS TEXT, WEIGHT TEXT, DATE TEXT, TIME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, ArrayList<Record> records, String date, String time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        for (Record record : records) {
            contentValues.put(COL_1, name);
            contentValues.put(COL_2, record.getExercise());
            contentValues.put(COL_3, record.getSets());

            contentValues.put(COL_4, "최소 -> " + record.getMinWeight() + ", 최대 -> " + record.getMaxWeight());
            contentValues.put(COL_5, date);
            contentValues.put(COL_6, time);
            long result = db.insert(TABLE_NAME, null, contentValues);
            if (result == -1) {
                return false; // 삽입 실패
            }
        }
        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public Integer deleteData(String name, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "NAME = ? AND DATE = ?", new String[]{name, date});
    }

    public int getDatesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT DATE) FROM " + TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public String getLatestTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        String latestTime = "";

        Cursor cursor = db.rawQuery("SELECT " + COL_6 + " FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToLast()) {
            int columnIndex = cursor.getColumnIndex(COL_6);
            if (columnIndex != -1) {
                latestTime = cursor.getString(columnIndex);
            }
            cursor.close();
        } else {
            latestTime = "운동 기록이 없습니다.";
        }

        return latestTime;
    }
}
