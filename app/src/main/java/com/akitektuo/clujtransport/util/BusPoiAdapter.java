package com.akitektuo.clujtransport.util;

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
import com.akitektuo.clujtransport.activity.MapActivity;

/**
 * Created by AoD Akitektuo on 10-Jun-16.
 */
public class BusPoiAdapter extends ArrayAdapter<BusPoiItem> {

    private Context context;
    private BusPoiItem[] items;

    public BusPoiAdapter(Context context, BusPoiItem[] objects) {
        super(context, R.layout.item_bus_info_line_poi, objects);
        this.context = context;
        items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_bus_info_line_poi, parent, false);

        final BusPoiItem item = items[position];
        ImageView imageBusType = (ImageView) view.findViewById(R.id.image_bus_poi);
        TextView textBusLine = (TextView) view.findViewById(R.id.text_line_bus_poi);
        TextView textBusHours = (TextView) view.findViewById(R.id.text_line_bus_hour_poi);

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
