package com.akitektuo.clujtransport.database.temp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.skobbler.ngx.SKCoordinate;

/**
 * Created by AoD Akitektuo on 11-Jun-16.
 */
public class StationHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database_stations.db";

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_QUERY = "CREATE TABLE " + StationContract.StationContractEntry.TABLE_NAME + " (" +
            StationContract.StationContractEntry.COLUMN_NAME_STATION + " TEXT," +
            StationContract.StationContractEntry.COLUMN_NAME_ROAD + " TEXT," +
            StationContract.StationContractEntry.COLUMN_NAME_LONG + " TEXT," +
            StationContract.StationContractEntry.COLUMN_NAME_LAT + " TEXT," +
            StationContract.StationContractEntry.COLUMN_NAME_LINES + " TEXT," +
            StationContract.StationContractEntry.COLUMN_NAME_TICKET + " TEXT" + ");";

//    public  static final String DATABASE_QUERY_UPDATE = "ALTER TABLE " + StationContract.StationContractEntry.TABLE_NAME + " ADD " + StationContract.StationContractEntry.COLUMN_NAME_TICKET + " TEXT";

    public StationHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_QUERY);
        //db.execSQL(DATABASE_QUERY_UPDATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addInformation(String station, String road, String lon, String lat, String lines, String tickets, SQLiteDatabase db) {
        if (isInfoAdded(station, road, lon, lat, lines, tickets) && !isStation(station)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_STATION, station);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_ROAD, road);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_LONG, lon);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_LAT, lat);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_LINES, lines);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_TICKET, tickets);
            db.insert(StationContract.StationContractEntry.TABLE_NAME, null, contentValues);
            return true;
        }
        return false;
    }

    public boolean isInfoAdded(String station, String road, String lon, String lat, String lines, String tickets) {
        if (station.equals("") || road.equals("") || lon.equals("") || lat.equals("") || lines.equals("") || tickets.equals("")) {
            return false;
        }
        return true;
    }

    public Cursor getInformation(SQLiteDatabase db) {
        String[] list = {StationContract.StationContractEntry.COLUMN_NAME_STATION,
                StationContract.StationContractEntry.COLUMN_NAME_ROAD,
                StationContract.StationContractEntry.COLUMN_NAME_LONG,
                StationContract.StationContractEntry.COLUMN_NAME_LAT,
                StationContract.StationContractEntry.COLUMN_NAME_LINES,
                StationContract.StationContractEntry.COLUMN_NAME_TICKET
        };
        return db.query(StationContract.StationContractEntry.TABLE_NAME, list, null, null, null, null, null);
    }

    public void deleteInformation(String station, SQLiteDatabase db) {
        String selection = StationContract.StationContractEntry.COLUMN_NAME_STATION + " LIKE ?";
        String[] selectionArgs = {station};
        db.delete(StationContract.StationContractEntry.TABLE_NAME, selection, selectionArgs);
    }

    public boolean isInformationForStation(String station, SQLiteDatabase db) {
        Cursor cursor = getInformation(db);
        if (cursor.moveToFirst()) {
            do {
                if (station.equals(cursor.getString(0))){
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    public boolean isStation(String station) {
        Cursor cursor = getInformation(getReadableDatabase());
        if (cursor.moveToFirst()) {
            String existingStation;
            do {
                existingStation = cursor.getString(0);
                if (station.equals(existingStation)) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    public boolean updateInformation(String oldStation, String station, String road, String lon, String lat, String lines, String ticket, SQLiteDatabase db) {
        if ((!isStation(station) || oldStation.equals(station)) && isInfoAdded(station, road, lon, lat, lines, ticket)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_STATION, station);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_ROAD, road);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_LONG, lon);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_LAT, lat);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_LINES, lines);
            contentValues.put(StationContract.StationContractEntry.COLUMN_NAME_TICKET, ticket);
            String selection = StationContract.StationContractEntry.COLUMN_NAME_STATION + " LIKE ?";
            String[] selectionArgs = {oldStation};
            db.update(StationContract.StationContractEntry.TABLE_NAME, contentValues, selection, selectionArgs);
            return true;
        }
        return false;
    }

    public Cursor getInformationForStation(String station, SQLiteDatabase db) {
        String[] results = {StationContract.StationContractEntry.COLUMN_NAME_STATION,
                StationContract.StationContractEntry.COLUMN_NAME_ROAD,
                StationContract.StationContractEntry.COLUMN_NAME_LONG,
                StationContract.StationContractEntry.COLUMN_NAME_LAT,
                StationContract.StationContractEntry.COLUMN_NAME_LINES,
                StationContract.StationContractEntry.COLUMN_NAME_TICKET
        };
        String selection = StationContract.StationContractEntry.COLUMN_NAME_STATION + " LIKE ?";
        String[] selectionArgs = {station};
        return db.query(StationContract.StationContractEntry.TABLE_NAME, results, selection, selectionArgs, null, null, null);
    }

    public Cursor getInformationSorted(SQLiteDatabase db) {
        String[] list = {StationContract.StationContractEntry.COLUMN_NAME_STATION,
                StationContract.StationContractEntry.COLUMN_NAME_ROAD,
                StationContract.StationContractEntry.COLUMN_NAME_LONG,
                StationContract.StationContractEntry.COLUMN_NAME_LAT,
                StationContract.StationContractEntry.COLUMN_NAME_LINES,
                StationContract.StationContractEntry.COLUMN_NAME_TICKET
        };
        return db.query(StationContract.StationContractEntry.TABLE_NAME, list, null, null, null, null, null);
    }

    public Cursor getInformationForCoordinates(SKCoordinate coordinate, SQLiteDatabase db) {
        String[] results = {StationContract.StationContractEntry.COLUMN_NAME_STATION,
                StationContract.StationContractEntry.COLUMN_NAME_ROAD,
                StationContract.StationContractEntry.COLUMN_NAME_LONG,
                StationContract.StationContractEntry.COLUMN_NAME_LAT,
                StationContract.StationContractEntry.COLUMN_NAME_LINES,
                StationContract.StationContractEntry.COLUMN_NAME_TICKET
        };
        String selection = StationContract.StationContractEntry.COLUMN_NAME_LONG + " LIKE ? AND " + StationContract.StationContractEntry.COLUMN_NAME_LAT + " LIKE ?";
        String lon = String.valueOf(coordinate.getLongitude());
        String lat = String.valueOf(coordinate.getLatitude());
        String[] selectionArgs = {lon, lat};
        return db.query(StationContract.StationContractEntry.TABLE_NAME, results, selection, selectionArgs, null, null, null);
    }

    public int getNumberOfStations() {
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
