package com.akitektuo.clujtransport.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akitektuo.clujtransport.R;
import com.akitektuo.clujtransport.activity.MapActivity;
import com.akitektuo.clujtransport.activity.StationsListActivity;
import com.akitektuo.clujtransport.activity.TicketsActivity;
import com.akitektuo.clujtransport.database.temp.BusHelper;
import com.akitektuo.clujtransport.database.temp.StationHelper;

public class StationInfoAdapter extends ArrayAdapter<StationInfoItem> {
    private static final int ITEM_BUS_HEIGHT = 92;
    public static int busesHeight;

    private Context context;
    private StationInfoItem[] items;
    private BusHelper busHelper;

    public StationInfoAdapter(Context context, StationInfoItem[] objects) {
        super(context, R.layout.item_station_info, objects);
        this.context = context;
        items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_station_info, parent, false);
        busHelper = new BusHelper(context);

        final StationInfoItem item = items[position];
        TextView textTitle = (TextView) view.findViewById(R.id.text_station_name);
        TextView textRoad = (TextView) view.findViewById(R.id.text_station_road);
        Button buttonRoute = (Button) view.findViewById(R.id.button_route_station);
        Button buttonTickets = (Button) view.findViewById(R.id.button_station_tickets);
        RelativeLayout layoutTickets = (RelativeLayout) view.findViewById(R.id.layout_tickets);

        Cursor cursor;
        String[] lines = item.getLines().split(" ");
        ListView listBuses = (ListView) view.findViewById(R.id.list_bus_lines);
        BusLineItem[] busLineItems = new BusLineItem[lines.length];
        for (int i = 0; i < lines.length; i++) {
            cursor = busHelper.getInformationForLine(lines[i], busHelper.getReadableDatabase());
            if (cursor.moveToFirst()) {
                if (cursor.getString(2).length() > 11) {
                    busLineItems[i] = new BusLineItem(Integer.parseInt(cursor.getString(1)),
                            cursor.getString(0), cursor.getString(2).substring(0,11), item.getName());
                } else {
                    busLineItems[i] = new BusLineItem(Integer.parseInt(cursor.getString(1)),
                            cursor.getString(0), cursor.getString(2), item.getName());
                }
            }
        }
        ArrayAdapter<BusLineItem> linesArrayAdapter = new BusLineAdapter(getContext(), busLineItems);
        listBuses.setAdapter(linesArrayAdapter);

        ViewGroup.LayoutParams params = listBuses.getLayoutParams();
        busesHeight = busLineItems.length * ITEM_BUS_HEIGHT;
        params.height = busesHeight;
        listBuses.setLayoutParams(params);
        listBuses.requestLayout();

        buttonRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRoute = new Intent(getContext(), MapActivity.class);
                        intentRoute.putExtra(StationsListActivity.FIRST_ROUTE_COORDINATES, item.getFirstCoordinate());
                        intentRoute.putExtra(StationsListActivity.SECOND_ROUTE_COORDINATES, item.getSecondCoordinate());
                        context.startActivity(intentRoute);
            }
        });

        buttonTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button_station_tickets) {
                    context.startActivity(new Intent(getContext(), TicketsActivity.class));
                }
            }
        });

        textTitle.setText(item.getName());
        textRoad.setText(item.getAddress());

        if (item.isTickets()) {
            layoutTickets.setVisibility(View.VISIBLE);
        } else {
            layoutTickets.setVisibility(View.GONE);
        }

        return view;
    }
}
