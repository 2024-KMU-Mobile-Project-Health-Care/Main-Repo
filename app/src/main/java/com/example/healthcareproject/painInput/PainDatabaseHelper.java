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
    private static final int DATABASE_VERSION = 2; // Incremented version

    private static final String TABLE_NAME = "PainInfo";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_PAIN_TYPE = "painType";
    private static final String COLUMN_PAIN_INTENSITY = "painIntensity";
    private static final String COLUMN_PREDICTED_DISEASE = "predictedDisease"; // New column

    public PainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_TIMESTAMP + " TEXT, "
                + COLUMN_PAIN_TYPE + " TEXT, "
                + COLUMN_PAIN_INTENSITY + " INTEGER, "
                + COLUMN_PREDICTED_DISEASE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) { // Handle upgrade to version 2
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_PREDICTED_DISEASE + " TEXT");
        }
    }

    public void insertPainInfo(String location, String timestamp, String painType, int painIntensity) {
        insertPainInfo(location, timestamp, painType, painIntensity, null);
    }

    public void insertPainInfo(String location, String timestamp, String painType, int painIntensity, String predictedDisease) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_PAIN_TYPE, painType);
        values.put(COLUMN_PAIN_INTENSITY, painIntensity);
        values.put(COLUMN_PREDICTED_DISEASE, predictedDisease);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Map<String, String>> getAllPainInfo() {
        List<Map<String, String>> painInfoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            int locationIndex = cursor.getColumnIndex(COLUMN_LOCATION);
            int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
            int painTypeIndex = cursor.getColumnIndex(COLUMN_PAIN_TYPE);
            int painIntensityIndex = cursor.getColumnIndex(COLUMN_PAIN_INTENSITY);
            int predictedDiseaseIndex = cursor.getColumnIndex(COLUMN_PREDICTED_DISEASE);

            if (locationIndex == -1 || timestampIndex == -1 || painTypeIndex == -1 || predictedDiseaseIndex == -1) {
                Log.e("DatabaseError", "One or more columns are missing in the database.");
            } else {
                do {
                    Map<String, String> painInfo = new HashMap<>();
                    painInfo.put("painLocation", cursor.getString(locationIndex));
                    painInfo.put("painStartTime", cursor.getString(timestampIndex));
                    painInfo.put("painType", cursor.getString(painTypeIndex));
                    painInfo.put("painIntensity", cursor.getString(painIntensityIndex));
                    painInfo.put("predictedDisease", cursor.getString(predictedDiseaseIndex));
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
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public void updatePredictedDisease(String timestamp, String predictedDisease) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PREDICTED_DISEASE, predictedDisease);
        db.update(TABLE_NAME, values, COLUMN_TIMESTAMP + " = ?", new String[]{timestamp});
        db.close();
    }

    public void printAllPainInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));
                String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                String painType = cursor.getString(cursor.getColumnIndex(COLUMN_PAIN_TYPE));
                int painIntensity = cursor.getInt(cursor.getColumnIndex(COLUMN_PAIN_INTENSITY));
                String predictedDisease = cursor.getString(cursor.getColumnIndex(COLUMN_PREDICTED_DISEASE));

                Log.d("PainDatabaseHelper", "ID: " + id + ", Location: " + location +
                        ", Timestamp: " + timestamp + ", PainType: " + painType +
                        ", PainIntensity: " + painIntensity + ", PredictedDisease: " + predictedDisease);
            } while (cursor.moveToNext());
        } else {
            Log.d("PainDatabaseHelper", "No records found in the database.");
        }

        cursor.close();
        db.close();
    }

}
