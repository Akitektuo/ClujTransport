package com.akitektuo.clujtransport.util;/*Copyright  © 2015 Telenav, Inc. All rights reserved. Telenav® is a registered trademark of Telenav, Inc.,Sunnyvale, California in the
United States and may be registered in other countries. Other names may be trademarks of their respective owners.*/


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.akitektuo.clujtransport.R;
import com.akitektuo.clujtransport.activity.LineActivity;

public class BusLineAdapter extends ArrayAdapter<BusLineItem> {

    private Context context;
    public static final String LINE = "line";
    public static final String STATION = "station";
    private BusLineItem[] items;

    public BusLineAdapter(Context context, BusLineItem[] objects) {
        super(context, R.layout.item_bus_info_line, objects);
        this.context = context;
        items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_bus_info_line, parent, false);

        final BusLineItem item = items[position];
        ImageView imageBusType = (ImageView) view.findViewById(R.id.image_bus);
        TextView textBusLine = (TextView) view.findViewById(R.id.text_line_bus);
        TextView textBusHours = (TextView) view.findViewById(R.id.text_line_bus_hour);
        Button buttonInfo = (Button) view.findViewById(R.id.button_show_bus_info);

        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLine = new Intent(getContext(), LineActivity.class);
                intentLine.putExtra(LINE, item.getLine());
                intentLine.putExtra(STATION, item.getStation());
                context.startActivity(intentLine);
            }
        });

        if (item != null) {
            switch (item.getBusType()) {
                case BusPoiItem.BUS:
                    imageBusType.setBackground(getContext().getDrawable(R.drawable.bus_blue));
                    break;
                case BusPoiItem.TROLLEY_BUS:
                    imageBusType.setBackground(getContext().getDrawable(R.drawable.trolleybus_blue));
                    break;
                case BusPoiItem.TRAM:
                    imageBusType.setBackground(getContext().getDrawable(R.drawable.tram_blue));
                    break;
                case BusPoiItem.MINIBUS:
                    imageBusType.setBackground(getContext().getDrawable(R.drawable.minibus_blue));
                    break;
            }

            textBusLine.setText(" " + item.getLine() + " ");
            textBusHours.setText(item.getTime());
        }

        return view;
    }
}
