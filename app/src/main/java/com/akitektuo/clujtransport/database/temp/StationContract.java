package com.akitektuo.clujtransport.database.temp;

import android.provider.BaseColumns;

/**
 * Created by AoD Akitektuo on 11-Jun-16.
 */
public class StationContract {

    public abstract class StationContractEntry implements BaseColumns {
        public static final String TABLE_NAME = "Stations";
        public static final String COLUMN_NAME_STATION = "Station";
        public static final String COLUMN_NAME_ROAD = "Road";
        public static final String COLUMN_NAME_LONG = "Longitude";
        public static final String COLUMN_NAME_LAT = "Latitude";
        public static final String COLUMN_NAME_LINES = "Lines";
        public static final String COLUMN_NAME_TICKET = "Ticket";
    }

}
