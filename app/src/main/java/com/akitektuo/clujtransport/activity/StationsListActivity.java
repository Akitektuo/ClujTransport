package com.akitektuo.clujtransport.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.akitektuo.clujtransport.R;
import com.akitektuo.clujtransport.database.temp.BusHelper;
import com.akitektuo.clujtransport.database.temp.StationHelper;
import com.akitektuo.clujtransport.util.BusLineAdapter;
import com.akitektuo.clujtransport.util.BusLineItem;
import com.akitektuo.clujtransport.util.StationInfoAdapter;
import com.akitektuo.clujtransport.util.StationInfoItem;

public class StationsListActivity extends Activity implements View.OnClickListener {
    public static final String FIRST_ROUTE_COORDINATES = "first_route_coordinates";

    public static final String SECOND_ROUTE_COORDINATES = "second_route_coordinates";

    private Button buttonBack;

    private Button buttonSearch;

    private AutoCompleteTextView completeTextViewSearch;

    private StationHelper stationHelper;

    private BusHelper busHelper;

    private ListView listStationsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_list);
        buttonBack = (Button) findViewById(R.id.button_stations_back);
        buttonSearch = (Button) findViewById(R.id.button_stations_search);
        completeTextViewSearch = (AutoCompleteTextView) findViewById(R.id.edit_text_stations_search);
        stationHelper = new StationHelper(this);
        SettingsActivity.refreshList(this, completeTextViewSearch);
        listStationsInfo = (ListView) findViewById(R.id.list_stations);

        if (getIntent() != null && getIntent().getExtras() != null) {
            String search = getIntent().getStringExtra(MapActivity.SEARCH);
            showResultsForSearch(search);
        } else {
            showAllStations();
        }

        buttonBack.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);
        stationHelper.close();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_stations_back:
                super.onBackPressed();
                break;
            case R.id.button_stations_search:
                if (!completeTextViewSearch.getText().toString().isEmpty()) {
                    showResultsForSearch(completeTextViewSearch.getText().toString());
                } else {
                    showAllStations();
                }
                break;
        }
    }

    private void showSearchedStation(String station) {
        stationHelper = new StationHelper(this);
        StationInfoItem[] stationInfoItems = new StationInfoItem[1];
        if (stationHelper.isStation(station)) {
            Cursor cursor = stationHelper.getInformationForStation(station, stationHelper.getReadableDatabase());
            if (cursor.moveToFirst()) {
                stationInfoItems[0] = new StationInfoItem(cursor.getString(0), cursor.getString(1), Boolean.parseBoolean(cursor.getString(5)), Double.parseDouble(cursor.getString(2)),
                        Double.parseDouble(cursor.getString(3)), cursor.getString(4));
            } else {
                return;
            }
            ArrayAdapter<StationInfoItem> stationsArrayAdapter = new StationInfoAdapter(this, stationInfoItems);
            listStationsInfo.setAdapter(stationsArrayAdapter);
        }
    }

    private void showSearchedStationsForLine(String line) {
        stationHelper = new StationHelper(this);
        busHelper = new BusHelper(this);
        String[] stations;
        StationInfoItem[] stationInfoItems;
        if (busHelper.isLine(line)) {
            Cursor cursorBus = busHelper.getInformationForLine(line, busHelper.getReadableDatabase());
            if (cursorBus.moveToFirst()) {
                if (cursorBus.getString(3).equals("null")) {
                    return;
                } else {
                    stations = cursorBus.getString(3).split(";");
                    stationInfoItems = new StationInfoItem[stations.length];
                    Cursor cursorStation;
                    for (int i = 0; i < stations.length; i++) {
                        cursorStation = stationHelper.getInformationForStation(stations[i], stationHelper.getReadableDatabase());
                        if (cursorStation.moveToFirst()) {
                            stationInfoItems[i] = new StationInfoItem(cursorStation.getString(0), cursorStation.getString(1), Boolean.parseBoolean(cursorStation.getString(5)), Double.parseDouble(cursorStation.getString(2)),
                                    Double.parseDouble(cursorStation.getString(3)), cursorStation.getString(4));
                        }
                    }
                }
            } else {
                return;
            }
            ArrayAdapter<StationInfoItem> stationsArrayAdapter = new StationInfoAdapter(this, stationInfoItems);
            listStationsInfo.setAdapter(stationsArrayAdapter);
        }
    }

    private boolean isLine(String search){
        for (int i = 0; i < 10; i++) {
            if (search.contains("" + i)) {
                return true;
            }
        }
        return false;
    }

    private void showResultsForSearch(String search) {
        if (isLine(search)) {
            showSearchedStationsForLine(search);
        } else {
            showSearchedStation(search);
        }
    }

    private void showAllStations() {
        StationInfoItem[] stationInfoItems = new StationInfoItem[stationHelper.getNumberOfStations()];
        Cursor cursor = stationHelper.getInformation(stationHelper.getReadableDatabase());
        if (cursor.moveToFirst()) {
            int stationNum = 0;
            do {
                stationInfoItems[stationNum] = new StationInfoItem(cursor.getString(0), cursor.getString(1), Boolean.parseBoolean(cursor.getString(5)), Double.parseDouble(cursor.getString(2)),
                        Double.parseDouble(cursor.getString(3)), cursor.getString(4));
                stationNum++;
            } while (cursor.moveToNext());
            ArrayAdapter<StationInfoItem> stationsArrayAdapter = new StationInfoAdapter(this, stationInfoItems);
            listStationsInfo.setAdapter(stationsArrayAdapter);
        }
    }

}
