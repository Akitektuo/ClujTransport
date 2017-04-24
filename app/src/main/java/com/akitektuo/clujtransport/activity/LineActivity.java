package com.akitektuo.clujtransport.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.akitektuo.clujtransport.R;
import com.akitektuo.clujtransport.database.temp.BusHelper;
import com.akitektuo.clujtransport.util.BusLineAdapter;

public class LineActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private TextView textLine;
    private static final String[] weekdaysHours = {" 04:58 "," 05:06 "," 05:14 "," 05:22 "," 05:30 "," 05:38 "," 05:46 "," 05:54 "," 06:02 "," 06:10 "," 06:18 "," 06:24 "," 06:30 "," 06:36 "," 06:42 ",
            " 06:48 "," 06:54 "," 07:00 "," 07:06 "," 07:12 "," 07:18 "," 07:24 "," 07:31 "," 07:37 "," 07:43 "," 07:50 "," 07:56 "," 08:02 "," 08:09 "," 08:15 "," 08:21 "," 08:28 "," 08:35 "," 08:43 ",
            " 08:50 "," 08:58 "," 09:05 "," 09:13 "," 09:20 "," 09:28 "," 09:35 "," 09:43 "," 09:50 "," 09:58 "," 10:05 "," 10:13 "," 10:20 "," 10:28 "," 10:35 "," 10:43 "," 10:50 "," 10:58 "," 11:05 ",
            " 11:13 "," 11:20 "," 11:28 "," 11:35 "," 11:43 "," 11:50 "," 11:58 "," 12:05 "," 12:13 "," 12:20 "," 12:28 "," 12:35 "," 12:43 "," 12:50 "," 12:57 "," 13:03 "," 13:09 "," 13:15 "," 13:21 ",
            " 13:27 "," 13:33 "," 13:39 "," 13:45 "," 13:51 "," 13:57 "," 14:03 "," 14:09 "," 14:15 "," 14:21 "," 14:27 "," 14:33 "," 14:39 "," 14:45 "," 14:51 "," 14:57 "," 15:03 "," 15:09 "," 15:15 ",
            " 15:21 "," 15:27 "," 15:33 "," 15:39 "," 15:45 "," 15:51 "," 15:57 "," 16:03 "," 16:09 "," 16:15 "," 16:21 "," 16:27 "," 16:33 "," 16:39 "," 16:45 "," 16:51 "," 16:57 "," 17:03 "," 17:09 ",
            " 17:15 "," 17:21 "," 17:27 "," 17:33 "," 17:39 "," 17:45 "," 17:51 "," 17:57 "," 18:03 "," 18:09 "," 18:15 "," 18:21 "," 18:28 "," 18:35 "," 18:43 "," 18:50 "," 18:58 "," 19:05 "," 19:13 ",
            " 19:21 "," 19:29 "," 19:38 "," 19:47 "," 19:56 "," 20:05 "," 20:14 "," 20:23 "," 20:32 "," 20:41 "," 20:50 "," 20:59 "," 21:08 "," 21:17 "," 21:26 "," 21:35 "," 21:44 "," 21:53 "," 22:03 ",
            " 22:13 "," 22:23 "," 22:33 "," 22:42 "};
    private Button buttonBack;
    private Button buttonShowOnMap;
    private TextView textStation;
    private GridView weekdays;
    private GridView saturday;
    private GridView sunday;
    private String line;
    private String station;
    private BusHelper busHelper;
    public static final String SHOW_REQUEST = "show_line_on_map";
    private boolean weekdaysOff = true;
    private boolean saturdayOff = true;
    private boolean sundayOff = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
//        textWeekdays = (TextView) findViewById(R.id.text_line_weekdays);
        textLine = (TextView) findViewById(R.id.text_line_title);
        buttonBack = (Button) findViewById(R.id.button_line_back);
        buttonShowOnMap = (Button) findViewById(R.id.button_line_map);
        textStation = (TextView) findViewById(R.id.text_line_info_station);
        weekdays = (GridView) findViewById(R.id.grid_line_weekdays);
        saturday = (GridView) findViewById(R.id.grid_line_saturday);
        sunday = (GridView) findViewById(R.id.grid_line_sunday);
        busHelper = new BusHelper(this);

        buttonBack.setOnClickListener(this);
        buttonShowOnMap.setOnClickListener(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            line = getIntent().getStringExtra(BusLineAdapter.LINE);
            station = getIntent().getStringExtra(BusLineAdapter.STATION);
        }

        textLine.setText("Line " + line);
        textStation.setText(getString(R.string.station) + " " + station);

        Cursor cursorBus = busHelper.getInformationForLine(line, busHelper.getReadableDatabase());
        if (cursorBus.moveToFirst()) {
            String weekdaysTime = cursorBus.getString(2);
            if (weekdaysTime.equals("null")) {
                weekdays.setAdapter(new ArrayAdapter<>(this, R.layout.cell, weekdaysHours));
                saturday.setAdapter(new ArrayAdapter<>(this, R.layout.cell, weekdaysHours));
                sunday.setAdapter(new ArrayAdapter<>(this, R.layout.cell, weekdaysHours));
            } else {
                String[] schedule = weekdaysTime.split(";");
                String[] weekdaysTimeArray = schedule[0].split(" ");
                weekdays.setAdapter(new ArrayAdapter<>(this, R.layout.cell, cleanArray(weekdaysTimeArray)));
                weekdaysTimeArray = schedule[1].split(" ");
                saturday.setAdapter(new ArrayAdapter<>(this, R.layout.cell, cleanArray(weekdaysTimeArray)));
                weekdaysTimeArray = schedule[2].split(" ");
                sunday.setAdapter(new ArrayAdapter<>(this, R.layout.cell, cleanArray(weekdaysTimeArray)));
            }
        }

        weekdays.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onClick(View view) {
        Intent showIntent = new Intent(this, MapActivity.class);
        switch (view.getId()) {
            case R.id.button_line_back:
                super.onBackPressed();
                break;
            case R.id.button_line_map:
                showIntent.putExtra(SHOW_REQUEST, line);
                startActivity(showIntent);
                break;
            case R.id.text_line_weekdays:
                if (weekdaysOff) {
                    weekdays.setVisibility(View.VISIBLE);
                    weekdaysOff = false;
                } else {
                    weekdays.setVisibility(View.GONE);
                    weekdaysOff = true;
                }
                break;
            case R.id.text_line_saturday:
                if (saturdayOff) {
                    saturday.setVisibility(View.VISIBLE);
                    saturdayOff = false;
                } else {
                    saturday.setVisibility(View.GONE);
                    saturdayOff = true;
                }
                break;
            case R.id.text_line_sunday:
                if (sundayOff) {
                    sunday.setVisibility(View.VISIBLE);
                    sundayOff = false;
                } else {
                    sunday.setVisibility(View.GONE);
                    sundayOff = true;
                }
                break;
        }
    }

    private String[] cleanArray(String[] stringArray) {
        if (stringArray.length > 0) {
            for (int i = 0; i < stringArray.length; i++) {
                stringArray[i] = " " + stringArray[i] + " ";
            }
        }
        return stringArray;
    }
}
