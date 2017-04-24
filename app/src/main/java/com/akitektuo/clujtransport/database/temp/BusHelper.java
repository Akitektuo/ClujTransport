package com.akitektuo.clujtransport.database.temp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AoD Akitektuo on 12-Jun-16.
 */
public class BusHelper extends SQLiteOpenHelper {

    public static final  String DATABASE_NAME = "database_bus.db";

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_QUERY = "CREATE TABLE " + BusContract.BusContractEntry.TABLE_NAME + " (" +
            BusContract.BusContractEntry.COLUMN_NAME_LINE + " TEXT," +
            BusContract.BusContractEntry.COLUMN_NAME_TYPE + " TEXT," +
            BusContract.BusContractEntry.COLUMN_NAME_TIME + " TEXT," +
            BusContract.BusContractEntry.COLUMN_NAME_STATIONS + " TEXT" + ");";

    public BusHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_QUERY);
//        db.execSQL("ALTER TABLE " + BusContract.BusContractEntry.TABLE_NAME + " ADD COLUMN " + BusContract.BusContractEntry.COLUMN_NAME_TINE_SAT + " TEXT");
//        db.execSQL("ALTER TABLE " + BusContract.BusContractEntry.TABLE_NAME + " ADD COLUMN " + BusContract.BusContractEntry.COLUMN_NAME_TINE_SUN + " TEXT");
//        db.execSQL("ALTER TABLE " + BusContract.BusContractEntry.TABLE_NAME + " ADD COLUMN " + BusContract.BusContractEntry.COLUMN_NAME_DRAW + " TEXT");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addInformation(String line, String type, String time, String stations, SQLiteDatabase db) {
        if (!isLine(line) && isInfoAdded(line, type, time, stations)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BusContract.BusContractEntry.COLUMN_NAME_LINE, line);
            contentValues.put(BusContract.BusContractEntry.COLUMN_NAME_TYPE, type);
            contentValues.put(BusContract.BusContractEntry.COLUMN_NAME_TIME, time);
            contentValues.put(BusContract.BusContractEntry.COLUMN_NAME_STATIONS, stations);
            db.insert(BusContract.BusContractEntry.TABLE_NAME, null, contentValues);
            return true;
        }
        return false;
    }

    public Cursor getInformation(SQLiteDatabase db) {
        String[] list = {BusContract.BusContractEntry.COLUMN_NAME_LINE,
                BusContract.BusContractEntry.COLUMN_NAME_TYPE,
                BusContract.BusContractEntry.COLUMN_NAME_TIME,
                BusContract.BusContractEntry.COLUMN_NAME_STATIONS};
        return db.query(BusContract.BusContractEntry.TABLE_NAME, list, null, null, null, null, null);
    }

    public boolean isLine(String line) {
        Cursor cursor = getInformation(getReadableDatabase());
        if (cursor.moveToFirst()) {
            do {
                String existingLine = cursor.getString(0);
                if (line.equals(existingLine)) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    public void deleteInformation(String line, SQLiteDatabase db) {
        String selection = BusContract.BusContractEntry.COLUMN_NAME_LINE + " LIKE ?";
        String[] selectionArgs = {line};
        db.delete(BusContract.BusContractEntry.TABLE_NAME, selection, selectionArgs);
    }

    public boolean isInformationForLine(String line, SQLiteDatabase db) {
        Cursor cursor = getInformation(db);
        if (cursor.moveToFirst()) {
            do {
                if (line.equals(cursor.getString(0))) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    public boolean updateInformation(String oldLine, String line, String type, String time, String stations, SQLiteDatabase db) {
        if (!isLine(line) || oldLine.equals(line) && isInfoAdded(line, type, time, stations)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BusContract.BusContractEntry.COLUMN_NAME_LINE, line);
            contentValues.put(BusContract.BusContractEntry.COLUMN_NAME_TYPE, type);
            contentValues.put(BusContract.BusContractEntry.COLUMN_NAME_TIME, time);
            contentValues.put(BusContract.BusContractEntry.COLUMN_NAME_STATIONS, stations);
            String selection = BusContract.BusContractEntry.COLUMN_NAME_LINE + " LIKE ?";
            String[] selectionArgs = {oldLine};
            db.update(BusContract.BusContractEntry.TABLE_NAME, contentValues, selection, selectionArgs);
            return true;
        }
        return false;
    }

    public Cursor getInformationForLine(String line, SQLiteDatabase db) {
        String[] results = {BusContract.BusContractEntry.COLUMN_NAME_LINE,
                BusContract.BusContractEntry.COLUMN_NAME_TYPE,
                BusContract.BusContractEntry.COLUMN_NAME_TIME,
                BusContract.BusContractEntry.COLUMN_NAME_STATIONS};
        String selection = BusContract.BusContractEntry.COLUMN_NAME_LINE + " LIKE ?";
        String[] selectionArgs = {line};
        return db.query(BusContract.BusContractEntry.TABLE_NAME, results, selection, selectionArgs, null, null, null);
    }

    public boolean isInfoAdded(String line, String type, String time, String stations) {
        if (line.equals("") || type.equals("") || time.equals("") || stations.equals("")) {
            return false;
        }
        return true;
    }

    public int getNumberOfLines() {
        Cursor cursor = getInformation(getReadableDatabase());
        int num = 0;
        if (cursor.moveToFirst()) {
            do {
                num++;
            } while (cursor.moveToNext());
        }
        return num;
    }

}
