package com.akitektuo.clujtransport.database.temp;

import android.provider.BaseColumns;

/**
 * Created by AoD Akitektuo on 12-Jun-16.
 */
public class BusContract {

    public abstract class BusContractEntry implements BaseColumns {
        public static final String TABLE_NAME = "Lines";
        public static final String COLUMN_NAME_LINE = "Number";
        public static final String COLUMN_NAME_TYPE = "Type";
        public static final String COLUMN_NAME_TIME = "Time";
        public static final String COLUMN_NAME_STATIONS = "Stations";
        public static final String COLUMN_NAME_TINE_SAT = "Saturday";
        public static final String COLUMN_NAME_TINE_SUN = "Sunday";
        public static final String COLUMN_NAME_DRAW = "Draw";
    }

}
