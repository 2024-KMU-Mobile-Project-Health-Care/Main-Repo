package com.example.healthcareproject.painInput;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PainDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PainData.db";
    private static final int DATABASE_VERSION = 1;

    // 테이블 및 컬럼 정의
    private static final String TABLE_NAME = "PainInfo";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_PAIN_TYPE = "painType";

    public PainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_TIMESTAMP + " TEXT, "
                + COLUMN_PAIN_TYPE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 데이터 삽입 메서드
    public void insertPainInfo(String location, String timestamp, String painType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_PAIN_TYPE, painType);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // 데이터 가져오기 메서드
    public List<Map<String, String>> getAllPainInfo() {
        List<Map<String, String>> painInfoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            int locationIndex = cursor.getColumnIndex(COLUMN_LOCATION);
            int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
            int painTypeIndex = cursor.getColumnIndex(COLUMN_PAIN_TYPE);

            if (locationIndex == -1 || timestampIndex == -1 || painTypeIndex == -1) {
                Log.e("DatabaseError", "One or more columns are missing in the database.");
            } else {
                do {
                    Map<String, String> painInfo = new HashMap<>();
                    painInfo.put("painLocation", cursor.getString(locationIndex));
                    painInfo.put("painStartTime", cursor.getString(timestampIndex));
                    painInfo.put("painType", cursor.getString(painTypeIndex));
                    painInfoList.add(painInfo);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return painInfoList;
    }

    public void deleteAllPainInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // 모든 행 삭제
        db.close();
    }

}
