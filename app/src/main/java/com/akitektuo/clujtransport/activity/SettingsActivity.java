package com.akitektuo.clujtransport.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akitektuo.clujtransport.R;
import com.akitektuo.clujtransport.database.temp.BusHelper;
import com.akitektuo.clujtransport.database.temp.StationHelper;

import java.util.ArrayList;

public class SettingsActivity extends Activity implements View.OnClickListener {
    private Button buttonAddStation;
    private Button buttonShowStation;
    private Button buttonRemoveStation;
    private AutoCompleteTextView completeTextDeleteStation;
    private StationHelper informationStationHelper;
    private String stationRemove;
    private AutoCompleteTextView completeTextUpdateStation;
    private Button buttonSearchForUpdateStation;
    private Button buttonUpdateStation;
    private Button buttonCancelUpdateStation;
    private EditText editTextUpdateStation;
    private EditText editTextUpdateRoad;
    private EditText editTextUpdateLong;
    private EditText editTextUpdateLat;
    private EditText editTextUpdateLines;
    private EditText editTextUpdateTickets;
    private String oldStation;
    private boolean visibleListStation = false;
    private Switch switchSettings;
    private LinearLayout layoutStation;
    private LinearLayout layoutLine;
    private Button buttonAddLine;
    private Button buttonShowLine;
    private Button buttonRemoveLine;
    private AutoCompleteTextView completeTextDeleteLine;
    private BusHelper informationLineHelper;
    private String lineRemove;
    private AutoCompleteTextView completeTextUpdateLine;
    private Button buttonSearchForUpdateLine;
    private Button buttonUpdateLine;
    private Button buttonCancelUpdateLine;
    private EditText editTextUpdateLine;
    private EditText editTextUpdateType;
    private EditText editTextUpdateTime;
    private EditText editTextUpdateStations;
    private String oldLine;
    private boolean visibleListLine = false;
    private Button buttonSyncStation;
    private Button buttonSyncLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // TODO: 13-Jun-16 Repair settings!!!

        informationStationHelper = new StationHelper(this);
        informationLineHelper = new BusHelper(this);

        switchSettings = (Switch) findViewById(R.id.switch_settings);

        layoutStation = (LinearLayout) findViewById(R.id.layout_settings_station);
        layoutLine = (LinearLayout) findViewById(R.id.layout_settings_line);

        completeTextDeleteStation = (AutoCompleteTextView) findViewById(R.id.edit_text_delete_station);
        completeTextUpdateStation = (AutoCompleteTextView) findViewById(R.id.edit_text_update_station);
        completeTextDeleteLine = (AutoCompleteTextView) findViewById(R.id.edit_text_delete_line);
        completeTextUpdateLine = (AutoCompleteTextView) findViewById(R.id.edit_text_update_line);

        editTextUpdateStation = (EditText) findViewById(R.id.edit_text_update_database_station);
        editTextUpdateRoad = (EditText) findViewById(R.id.edit_text_update_database_road);
        editTextUpdateLong = (EditText) findViewById(R.id.edit_text_update_database_long);
        editTextUpdateLat = (EditText) findViewById(R.id.edit_text_update_database_lat);
        editTextUpdateLines = (EditText) findViewById(R.id.edit_text_update_database_lines);
        editTextUpdateLine = (EditText) findViewById(R.id.edit_text_update_database_line);
        editTextUpdateType = (EditText) findViewById(R.id.edit_text_update_database_type);
        editTextUpdateTime = (EditText) findViewById(R.id.edit_text_update_database_time);
        editTextUpdateStations = (EditText) findViewById(R.id.edit_text_update_database_stations);
        editTextUpdateTickets = (EditText) findViewById(R.id.edit_text_update_database_tickets);

        buttonAddStation = (Button) findViewById(R.id.button_save_station);
        buttonShowStation = (Button) findViewById(R.id.button_show_station);
        buttonRemoveStation = (Button) findViewById(R.id.button_remove_station);
        buttonSearchForUpdateStation = (Button) findViewById(R.id.button_update_station);
        buttonUpdateStation = (Button) findViewById(R.id.button_save_changes);
        buttonCancelUpdateStation = (Button) findViewById(R.id.button_cancel_update);
        buttonAddLine = (Button) findViewById(R.id.button_save_line);
        buttonShowLine = (Button) findViewById(R.id.button_show_line);
        buttonRemoveLine = (Button) findViewById(R.id.button_remove_line);
        buttonSearchForUpdateLine = (Button) findViewById(R.id.button_update_line);
        buttonUpdateLine = (Button) findViewById(R.id.button_save_changes_line);
        buttonCancelUpdateLine  = (Button) findViewById(R.id.button_cancel_update_line);
        buttonSyncStation = (Button) findViewById(R.id.button_database_sync_station);
        buttonSyncLine = (Button) findViewById(R.id.button_database_sync_line);

        buttonAddStation.setOnClickListener(this);
        buttonShowStation.setOnClickListener(this);
        buttonRemoveStation.setOnClickListener(this);
        buttonSearchForUpdateStation.setOnClickListener(this);
        buttonUpdateStation.setOnClickListener(this);
        buttonCancelUpdateStation.setOnClickListener(this);
        buttonAddLine.setOnClickListener(this);
        buttonShowLine.setOnClickListener(this);
        buttonRemoveLine.setOnClickListener(this);
        buttonSearchForUpdateLine.setOnClickListener(this);
        buttonUpdateLine.setOnClickListener(this);
        buttonCancelUpdateLine.setOnClickListener(this);
        buttonSyncStation.setOnClickListener(this);
        buttonSyncLine.setOnClickListener(this);

        refreshLists();

        if (switchSettings.isChecked()) {
            layoutLine.setVisibility(View.VISIBLE);
            layoutStation.setVisibility(View.GONE);
        } else {
            layoutLine.setVisibility(View.GONE);
            layoutStation.setVisibility(View.VISIBLE);
        }

        switchSettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutLine.setVisibility(View.VISIBLE);
                    layoutStation.setVisibility(View.GONE);
                } else {
                    layoutLine.setVisibility(View.GONE);
                    layoutStation.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        String station, road, lon, lat, lines, line, type, time, stations, tickets;
        Cursor cursor;
        switch (view.getId()) {
            case R.id.button_save_station:

                EditText editTextAddStation = (EditText) findViewById(R.id.edit_text_add_station);
                station = editTextAddStation.getText().toString();

                EditText editTextAddRoad = (EditText) findViewById(R.id.edit_text_add_road);
                road = editTextAddRoad.getText().toString();

                EditText editTextAddLong = (EditText) findViewById(R.id.edit_text_add_long);
                lon = editTextAddLong.getText().toString();

                EditText editTextAddLat = (EditText) findViewById(R.id.edit_text_add_lat);
                lat = editTextAddLat.getText().toString();

                EditText editTextAddLines = (EditText) findViewById(R.id.edit_text_add_lines);
                lines = editTextAddLines.getText().toString();

                EditText editTextAddTickets = (EditText) findViewById(R.id.edit_text_add_tickets);
                tickets = editTextAddTickets.getText().toString();

                SQLiteDatabase database = informationStationHelper.getWritableDatabase();

                if (informationStationHelper.addInformation(station, road, lon, lat, lines, tickets, database)) {
                    Toast.makeText(this, "Station saved", Toast.LENGTH_LONG).show();
                    editTextAddStation.setText("");
                    editTextAddRoad.setText("");
                    editTextAddLong.setText("");
                    editTextAddLat.setText("");
                    editTextAddLines.setText("");
                    editTextAddTickets.setText("");
                } else {
                    Toast.makeText(this, "Please fill in all the gaps", Toast.LENGTH_LONG).show();
                }

                refreshStationList();

                informationStationHelper.close();
                break;
            case R.id.button_show_station:
                if (visibleListStation) {
                    TextView textStationList = (TextView) findViewById(R.id.text_station_database_list);
                    textStationList.setText("");
                    buttonShowStation.setText(R.string.see_all_stations);
                    visibleListStation = false;
                } else {
                    cursor = informationStationHelper.getInformation(informationStationHelper.getReadableDatabase());
                    if (cursor.moveToFirst()) {
                        TextView textStationList = (TextView) findViewById(R.id.text_station_database_list);
                        StringBuilder stringBuilder = new StringBuilder();
                        do {
                            station = cursor.getString(0);
                            road = cursor.getString(1);
                            lon = cursor.getString(2);
                            lat = cursor.getString(3);
                            lines = cursor.getString(4);
                            tickets = cursor.getString(5);
                            stringBuilder.append("\nStation ").append(station).append("\n").append(road).append("\nLongitude and Latitude\n").append(lon).append(" ").append(lat)
                                    .append("\nLines\n").append(lines).append("\nTickets available - ").append(tickets).append("\n");
                        } while (cursor.moveToNext());
                        textStationList.setText(stringBuilder);
                        buttonShowStation.setText("Hide stations");
                        visibleListStation = true;
                    } else {
                        Toast.makeText(this, "The database is empty", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.button_remove_station:
                stationRemove = completeTextDeleteStation.getText().toString();
                if (!stationRemove.equals("")) {
                    if (informationStationHelper.isInformationForStation(stationRemove, informationStationHelper.getReadableDatabase())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setTitle("Delete Station");
                        builder.setMessage("Are you sure you want to delete this station?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                completeTextDeleteStation.setText("");
                                informationStationHelper.deleteInformation(stationRemove, informationStationHelper.getWritableDatabase());
                                refreshStationList();
                                Toast.makeText(getBaseContext(), "Station deleted successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(this, "This station doesn't exist in database", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "You didn't type a station", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_update_station:
                if (completeTextUpdateStation.getText().toString().equals("")) {
                    Toast.makeText(this, "Type a station", Toast.LENGTH_LONG).show();
                } else {
                    cursor = informationStationHelper.getInformationForStation(completeTextUpdateStation.getText().toString(), informationStationHelper.getReadableDatabase());
                    if (cursor.moveToFirst()) {
                        oldStation = completeTextUpdateStation.getText().toString();
                        editTextUpdateStation.setText(cursor.getString(0));
                        editTextUpdateRoad.setText(cursor.getString(1));
                        editTextUpdateLong.setText(cursor.getString(2));
                        editTextUpdateLat.setText(cursor.getString(3));
                        editTextUpdateLines.setText(cursor.getString(4));
                        editTextUpdateTickets.setText(cursor.getString(5));
                        editTextUpdateStation.setVisibility(View.VISIBLE);
                        editTextUpdateRoad.setVisibility(View.VISIBLE);
                        editTextUpdateLong.setVisibility(View.VISIBLE);
                        editTextUpdateLat.setVisibility(View.VISIBLE);
                        editTextUpdateLines.setVisibility(View.VISIBLE);
                        editTextUpdateTickets.setVisibility(View.VISIBLE);
                        buttonUpdateStation.setVisibility(View.VISIBLE);
                        buttonCancelUpdateStation.setVisibility(View.VISIBLE);
                        completeTextUpdateStation.setVisibility(View.GONE);
                        buttonSearchForUpdateStation.setVisibility(View.GONE);
                        completeTextUpdateStation.setText("");
                    } else {
                        Toast.makeText(this, "Non existent in database", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.button_save_changes:
                if (informationStationHelper.updateInformation(oldStation, editTextUpdateStation.getText().toString(), editTextUpdateRoad.getText().toString(), editTextUpdateLong.getText().toString(),
                        editTextUpdateLat.getText().toString(), editTextUpdateLines.getText().toString(), editTextUpdateTickets.getText().toString(), informationStationHelper.getWritableDatabase())) {
                    refreshStationList();
                    Toast.makeText(this, "Update successful", Toast.LENGTH_LONG).show();
                    editTextUpdateStation.setVisibility(View.GONE);
                    editTextUpdateRoad.setVisibility(View.GONE);
                    editTextUpdateLong.setVisibility(View.GONE);
                    editTextUpdateLat.setVisibility(View.GONE);
                    editTextUpdateLines.setVisibility(View.GONE);
                    editTextUpdateTickets.setVisibility(View.GONE);
                    buttonUpdateStation.setVisibility(View.GONE);
                    buttonCancelUpdateStation.setVisibility(View.GONE);
                    completeTextUpdateStation.setVisibility(View.VISIBLE);
                    buttonSearchForUpdateStation.setVisibility(View.VISIBLE);
                    editTextUpdateStation.setText("");
                    editTextUpdateRoad.setText("");
                    editTextUpdateLong.setText("");
                    editTextUpdateLat.setText("");
                    editTextUpdateLines.setText("");
                    editTextUpdateTickets.setText("");
                } else {
                    Toast.makeText(this, "The station already exists", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_cancel_update:
                editTextUpdateStation.setVisibility(View.GONE);
                editTextUpdateRoad.setVisibility(View.GONE);
                editTextUpdateLong.setVisibility(View.GONE);
                editTextUpdateLat.setVisibility(View.GONE);
                editTextUpdateLines.setVisibility(View.GONE);
                editTextUpdateTickets.setVisibility(View.GONE);
                buttonUpdateStation.setVisibility(View.GONE);
                buttonCancelUpdateStation.setVisibility(View.GONE);
                completeTextUpdateStation.setVisibility(View.VISIBLE);
                buttonSearchForUpdateStation.setVisibility(View.VISIBLE);
                editTextUpdateStation.setText("");
                editTextUpdateRoad.setText("");
                editTextUpdateLong.setText("");
                editTextUpdateLat.setText("");
                editTextUpdateLines.setText("");
                editTextUpdateTickets.setText("");
                break;
            case R.id.button_save_line:

                EditText editTextAddLine = (EditText) findViewById(R.id.edit_text_add_line);
                line = editTextAddLine.getText().toString();

                EditText editTextAddType = (EditText) findViewById(R.id.edit_text_add_type);
                type = editTextAddType.getText().toString();

                EditText editTextAddTime = (EditText) findViewById(R.id.edit_text_add_time);
                time = editTextAddTime.getText().toString();

                EditText editTextAddStations = (EditText) findViewById(R.id.edit_text_add_stations);
                stations = editTextAddStations.getText().toString();

                if (informationLineHelper.addInformation(line, type, time, stations, informationLineHelper.getWritableDatabase())) {
                    Toast.makeText(this, "Line saved", Toast.LENGTH_LONG).show();
                    editTextAddLine.setText("");
                    editTextAddType.setText("");
                    editTextAddTime.setText("");
                    editTextAddStations.setText("");
                } else {
                    Toast.makeText(this, "Please fill in all the gaps", Toast.LENGTH_LONG).show();
                }

                refreshLineList();

                informationLineHelper.close();
                break;
            case R.id.button_database_sync_station:
                AlertDialog.Builder builderSyncStation = new AlertDialog.Builder(SettingsActivity.this);
                builderSyncStation.setTitle("Sync database");
                builderSyncStation.setMessage("Make sure you are connected to localhost");
                builderSyncStation.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "This will sync the data", Toast.LENGTH_LONG).show();
                    }
                });
                builderSyncStation.setNegativeButton("Cancel", null);
                AlertDialog alertDialogSyncStation = builderSyncStation.create();
                alertDialogSyncStation.show();
                break;
            case R.id.button_show_line:
                if (visibleListLine) {
                    TextView textLineList = (TextView) findViewById(R.id.text_line_database_list);
                    textLineList.setText("");
                    buttonShowLine.setText(R.string.see_all_lines);
                    visibleListLine = false;
                } else {
                    cursor = informationLineHelper.getInformation(informationLineHelper.getReadableDatabase());
                    if (cursor.moveToFirst()) {
                        TextView textLineList = (TextView) findViewById(R.id.text_line_database_list);
                        StringBuilder stringBuilder = new StringBuilder();
                        do {
                            line = cursor.getString(0);
                            type = cursor.getString(1);
                            time = cursor.getString(2);
                            stations = cursor.getString(3);
                            stringBuilder.append("\nLine ").append(line).append("\nType ").append(type).append("\nTime\n").append(time)
                                    .append("\nStations\n").append(stations).append("\n");
                        } while (cursor.moveToNext());
                        textLineList.setText(stringBuilder);
                        buttonShowLine.setText("Hide lines");
                        visibleListLine = true;
                    } else {
                        Toast.makeText(this, "The database is empty", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.button_remove_line:
                lineRemove = completeTextDeleteLine.getText().toString();
                if (!lineRemove.equals("")) {
                    if (informationLineHelper.isInformationForLine(lineRemove, informationLineHelper.getReadableDatabase())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setTitle("Delete Line");
                        builder.setMessage("Are you sure you want to delete this line?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                completeTextDeleteLine.setText("");
                                informationLineHelper.deleteInformation(lineRemove, informationLineHelper.getWritableDatabase());
                                refreshLineList();
                                Toast.makeText(getBaseContext(), "Line deleted successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(this, "This line doesn't exist in database", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "You didn't type a line", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_update_line:
                if (completeTextUpdateLine.getText().toString().equals("")) {
                    Toast.makeText(this, "Type a line", Toast.LENGTH_LONG).show();
                } else {
                    cursor = informationLineHelper.getInformationForLine(completeTextUpdateLine.getText().toString(), informationLineHelper.getReadableDatabase());
                    if (cursor.moveToFirst()) {
                        oldLine = completeTextUpdateLine.getText().toString();
                        editTextUpdateLine.setText(cursor.getString(0));
                        editTextUpdateType.setText(cursor.getString(1));
                        editTextUpdateTime.setText(cursor.getString(2));
                        editTextUpdateStations.setText(cursor.getString(3));
                        editTextUpdateLine.setVisibility(View.VISIBLE);
                        editTextUpdateType.setVisibility(View.VISIBLE);
                        editTextUpdateTime.setVisibility(View.VISIBLE);
                        editTextUpdateStations.setVisibility(View.VISIBLE);
                        buttonUpdateLine.setVisibility(View.VISIBLE);
                        buttonCancelUpdateLine.setVisibility(View.VISIBLE);
                        completeTextUpdateLine.setVisibility(View.GONE);
                        buttonSearchForUpdateLine.setVisibility(View.GONE);
                        completeTextUpdateLine.setText("");
                    } else {
                        Toast.makeText(this, "Non existent in database", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.button_save_changes_line:
                if (informationLineHelper.updateInformation(oldLine, editTextUpdateLine.getText().toString(), editTextUpdateType.getText().toString(), editTextUpdateTime.getText().toString(),
                        editTextUpdateStations.getText().toString(), informationLineHelper.getWritableDatabase())) {
                    refreshLineList();
                    Toast.makeText(this, "Update successful", Toast.LENGTH_LONG).show();
                    editTextUpdateLine.setVisibility(View.GONE);
                    editTextUpdateType.setVisibility(View.GONE);
                    editTextUpdateTime.setVisibility(View.GONE);
                    editTextUpdateStations.setVisibility(View.GONE);
                    buttonUpdateLine.setVisibility(View.GONE);
                    buttonCancelUpdateLine.setVisibility(View.GONE);
                    completeTextUpdateLine.setVisibility(View.VISIBLE);
                    buttonSearchForUpdateLine.setVisibility(View.VISIBLE);
                    editTextUpdateLine.setText("");
                    editTextUpdateType.setText("");
                    editTextUpdateTime.setText("");
                    editTextUpdateStations.setText("");
                } else {
                    Toast.makeText(this, "The line already exists", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_cancel_update_line:
                editTextUpdateLine.setVisibility(View.GONE);
                editTextUpdateType.setVisibility(View.GONE);
                editTextUpdateTime.setVisibility(View.GONE);
                editTextUpdateStations.setVisibility(View.GONE);
                buttonUpdateLine.setVisibility(View.GONE);
                buttonCancelUpdateLine.setVisibility(View.GONE);
                completeTextUpdateLine.setVisibility(View.VISIBLE);
                buttonSearchForUpdateLine.setVisibility(View.VISIBLE);
                editTextUpdateLine.setText("");
                editTextUpdateType.setText("");
                editTextUpdateTime.setText("");
                editTextUpdateStations.setText("");
                break;
            case R.id.button_database_sync_line:
                AlertDialog.Builder builderSyncLine = new AlertDialog.Builder(SettingsActivity.this);
                builderSyncLine.setTitle("Sync database");
                builderSyncLine.setMessage("Make sure you are connected to localhost");
                builderSyncLine.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "This will sync the data", Toast.LENGTH_LONG).show();
                    }
                });
                builderSyncLine.setNegativeButton("Cancel", null);
                AlertDialog alertDialogSyncLine = builderSyncLine.create();
                alertDialogSyncLine.show();
                break;
        }
    }

    private void refreshStationList() {
        Cursor cursor = informationStationHelper.getInformation(informationStationHelper.getWritableDatabase());
        if (cursor.moveToFirst()) {
            ArrayList<String> stations = new ArrayList<>();
            do {
                String station = cursor.getString(0);
                stations.add(station);
            } while (cursor.moveToNext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stations);
            completeTextUpdateStation.setAdapter(adapter);
            completeTextDeleteStation.setAdapter(adapter);
        }
    }

    private void refreshLineList() {
        Cursor cursor = informationLineHelper.getInformation(informationLineHelper.getWritableDatabase());
        if (cursor.moveToFirst()) {
            ArrayList<String> lines = new ArrayList<>();
            do {
                String line = cursor.getString(0);
                lines.add(line);
            } while (cursor.moveToNext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lines);
            completeTextDeleteLine.setAdapter(adapter);
            completeTextUpdateLine.setAdapter(adapter);
        }
    }

    public static void refreshList(Context context, AutoCompleteTextView completeTextView) {
        ArrayList<String> list = new ArrayList<>();
        StationHelper informationStationHelper = new StationHelper(context);
        Cursor cursorStation = informationStationHelper.getInformation(informationStationHelper.getWritableDatabase());
        if (cursorStation.moveToFirst()) {
            do {
                String station = cursorStation.getString(0);
                list.add(station);
            } while (cursorStation.moveToNext());
        }
        BusHelper informationLineHelper = new BusHelper(context);
        Cursor cursorLine = informationLineHelper.getInformation(informationLineHelper.getWritableDatabase());
        if (cursorLine.moveToFirst()) {
            do {
                String line = cursorLine.getString(0);
                list.add(line);
            } while (cursorLine.moveToNext());
        }
        if (list.isEmpty()) {
            list.add("null");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
        completeTextView.setAdapter(adapter);
    }

    private void refreshLists() {
        refreshStationList();
        refreshLineList();
    }
}
