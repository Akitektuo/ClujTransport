package com.akitektuo.clujtransport.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akitektuo.clujtransport.R;
import com.akitektuo.clujtransport.database.temp.BusHelper;
import com.akitektuo.clujtransport.database.temp.StationHelper;
import com.akitektuo.clujtransport.util.AppUtils;
import com.akitektuo.clujtransport.util.BusPoiAdapter;
import com.akitektuo.clujtransport.util.BusPoiItem;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCalloutView;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;

import java.util.Arrays;

import it.sephiroth.android.library.widget.HListView;

public class MapActivity extends Activity implements SKMapSurfaceListener, SKCurrentPositionListener,
        SKRouteListener, SKNavigationListener, View.OnClickListener {
    private static final int STATION_NUM_LINE_3 = 18;

    private static final int TICKET_PLACES = 18;
    private static final int NAVIGATION_ANNOTATION = 19;

    private static final int DELAY_LENGTH = 2000;

//    private static final int NAVIGATION_STARTED_MODE = 1;

    private static SKCoordinate lastKnowCoordinates;

    private static boolean lockedUI = false;

    private SKMapSurfaceView mapView;

    private SKMapViewHolder mapHolder;

    private SKCurrentPositionProvider currentPositionProvider;

    private SKPosition currentPosition;

    private Button button_menu;

    private Button button_search;

    private Button button_location;

    private Button button_bus;

    private Button button_ticket;

    private Button buttonCancelNavigation;

    private Button buttonCancelTicket;

    private Button buttonRouteTicket;

    private Button buttonInfoTicket;

    private Button buttonCancelStation;

    private Button buttonRouteStation;

    private Button buttonInfoStation;

    private ImageView imageViewTicketPoi;

    private ImageView imageViewStationPoi;

    private RelativeLayout layoutStationInfo;

    private RelativeLayout layoutTicketInfo;

    private ImageView imageViewSearch;

    private boolean created = false;

    private boolean navigationStarted = false;

    private boolean buttons = true;

    private boolean busButton = true;

    private SKCalloutView mapPopup;

//    private TextView textAnnotation;

//    private boolean annotationPopup = false;

    private boolean ticketButton = true;

    private AutoCompleteTextView completeTextViewSearch;

    private SKCoordinate coordinateTicketPoi;

    private SKCoordinate coordinateStationPoi;

    private StationHelper stationHelper;

    private BusHelper busHelper;

    private int createdAnnotations = 0;

    public static final String SEARCH = "search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        SKMapViewHolder mapViewGroup = (SKMapViewHolder) findViewById(R.id.map_surface_holder);
        mapViewGroup.getMapSurfaceView();
        mapHolder = (SKMapViewHolder) findViewById(R.id.map_surface_holder);
        mapHolder.setMapSurfaceListener(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mapPopup = mapViewGroup.getCalloutView();
//        popupTitleView = (TextView) view.findViewById(R.id.top_text);
//        popupDescriptionView = (TextView) view.findViewById(R.id.bottom_text);
//        textAnnotation = (TextView) findViewById(R.id.text_annotation_title);
        imageViewTicketPoi = (ImageView) findViewById(R.id.image_background_tickets);
        layoutTicketInfo = (RelativeLayout) findViewById(R.id.layout_poi_ticket);
        imageViewStationPoi = (ImageView) findViewById(R.id.image_background_stations);
        layoutStationInfo = (RelativeLayout) findViewById(R.id.layout_poi_station);

        currentPositionProvider = new SKCurrentPositionProvider(this);
        currentPositionProvider.setCurrentPositionListener(this);
        currentPositionProvider.requestLocationUpdates(AppUtils.hasGpsModule(this), AppUtils.hasNetworkModule(this), false);

        button_menu = (Button) findViewById(R.id.button_menu);
        button_search = (Button) findViewById(R.id.button_search);
        button_bus = (Button) findViewById(R.id.button_locate_bus);
        button_location = (Button) findViewById(R.id.button_location);
        button_ticket = (Button) findViewById(R.id.button_locate_ticket);
        buttonCancelNavigation = (Button) findViewById(R.id.button_cancel_navigation);
        imageViewSearch = (ImageView) findViewById(R.id.image_view_search);
        completeTextViewSearch = (AutoCompleteTextView) findViewById(R.id.edit_text_search);
        buttonCancelTicket = (Button) findViewById(R.id.button_cancel_info_ticket_poi);
        buttonRouteTicket = (Button) findViewById(R.id.button_route_from_ticket_poi);
        buttonInfoTicket = (Button) findViewById(R.id.button_more_info_ticket_poi);
        buttonCancelStation = (Button) findViewById(R.id.button_cancel_info_station_poi);
        buttonRouteStation = (Button) findViewById(R.id.button_route_from_station_poi);
        buttonInfoStation = (Button) findViewById(R.id.button_more_info_station_poi);

        SettingsActivity.refreshList(this, completeTextViewSearch);

        button_menu.setOnClickListener(this);
        button_search.setOnClickListener(this);
        button_location.setOnClickListener(this);
        button_ticket.setOnClickListener(this);
        button_bus.setOnClickListener(this);
        buttonCancelNavigation.setOnClickListener(this);
        buttonCancelTicket.setOnClickListener(this);
        buttonRouteTicket.setOnClickListener(this);
        buttonInfoTicket.setOnClickListener(this);
        buttonCancelStation.setOnClickListener(this);
        buttonRouteStation.setOnClickListener(this);
        buttonInfoStation.setOnClickListener(this);

        stationHelper = new StationHelper(this);
        busHelper = new BusHelper(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getIntent().getDoubleExtra(StationsListActivity.FIRST_ROUTE_COORDINATES, 0) != 0) {
                    startNavigation(new SKCoordinate(getIntent().getDoubleExtra(StationsListActivity.FIRST_ROUTE_COORDINATES, 0), getIntent().getDoubleExtra(StationsListActivity
                            .SECOND_ROUTE_COORDINATES, 0)));
                    imageViewSearch.setVisibility(View.GONE);
                    completeTextViewSearch.setVisibility(View.GONE);
                    button_menu.setVisibility(View.GONE);
                    button_search.setVisibility(View.GONE);
                    button_location.setVisibility(View.GONE);
                    button_bus.setVisibility(View.GONE);
                    button_ticket.setVisibility(View.GONE);
                    buttons = false;
                    lockedUI = true;
                    buttonCancelNavigation.setVisibility(View.VISIBLE);

                } else if (getIntent().getStringExtra(LineActivity.SHOW_REQUEST) != null) {
                    mapView.deleteAllAnnotationsAndCustomPOIs();
                    if (busButton) {
                        showLine(getIntent().getStringExtra(LineActivity.SHOW_REQUEST));
                        button_bus.setBackgroundResource(R.drawable.cancel_circle_blue);
                        busButton = false;
                    }
                }
            }
        }, DELAY_LENGTH);

        imageViewSearch.setVisibility(View.GONE);
        completeTextViewSearch.setVisibility(View.GONE);
        button_menu.setVisibility(View.GONE);
        button_search.setVisibility(View.GONE);
        button_location.setVisibility(View.GONE);
        button_bus.setVisibility(View.GONE);
        button_ticket.setVisibility(View.GONE);
        buttons = false;

    }

//    public void openDrawer(){
//        mDrawerLayout.openDrawer(mDrawerLayout);
//        openedDrawer = true;
//
//    }
//
//    public void closeDrawer() {
//        mDrawerLayout.closeDrawer(mDrawerLayout);
//        openedDrawer = false;
//    }
//
//    private void addDrawerItems() {
//        String[] menuArray = { "Route", "Bus Stations", "Tickets", "Settings", "Help" };
//        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menuArray);
//        mDrawerList.setAdapter(mAdapter);
//    }

    @Override
    public void onClick(View view) {
        Intent menuIntent = new Intent(this, MenuActivity.class);
        Intent stationSearch = new Intent(this, StationsListActivity.class);
        switch (view.getId()) {
            case R.id.button_menu:
                startActivity(menuIntent);
//                if (currentPosition != null && created) {
//                    SKAnnotation annotationStart = new SKAnnotation(1);
//                    SKCoordinate annotationStartCoordinate = new SKCoordinate(23.625469, 46.771445);
//                    annotationStart.setLocation(annotationStartCoordinate);
//                    annotationStart.setMininumZoomLevel(5);
//                    annotationStart.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_RED);
//                    mapView.addAnnotation(annotationStart, SKAnimationSettings.ANIMATION_PIN_DROP);
//
//                    SKAnnotation annotationFinish = new SKAnnotation(1);
//                    SKCoordinate annotationFinishCoordinate = new SKCoordinate(23.593537, 46.773542);
//                    annotationFinish.setLocation(annotationFinishCoordinate);
//                    annotationFinish.setMininumZoomLevel(5);
//                    annotationFinish.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_DESTINATION_FLAG);
//                    mapView.addAnnotation(annotationFinish, SKAnimationSettings.ANIMATION_PIN_DROP);
//                    launchRouteCalculation(annotationStartCoordinate, annotationFinishCoordinate);
//
//                    action = true;
//                } else {
//                    Toast.makeText(this, "Please wait for the GPS to get your location", Toast.LENGTH_LONG);
//                }
                break;
            case R.id.button_search:
                if (!completeTextViewSearch.getText().toString().isEmpty()) {
                    stationSearch.putExtra(SEARCH, completeTextViewSearch.getText().toString());
                    startActivity(stationSearch);
                    completeTextViewSearch.setText("");
                }
                break;
            case R.id.button_location:
                if (currentPosition != null && created) {
//                    mapView.centerMapOnPosition(currentPosition.getCoordinate());
                    mapView.centerMapOnCurrentPositionSmooth(17, 500);
                    Toast.makeText(this, "This is your location", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Searching GPS location...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_locate_bus:
                if (ticketButton) {
                    if (busButton) {
                        showAllLines();
                        button_bus.setBackgroundResource(R.drawable.cancel_circle_blue);
                        busButton = false;
                    } else {
                        for (int i = 0; i < createdAnnotations; i++) {
                            mapView.deleteAnnotation(i);
                        }
                        button_bus.setBackgroundResource(R.drawable.bus_circle_blue);
                        busButton = true;
                    }
                }
                break;
            case R.id.button_locate_ticket:
                if (busButton) {
                    if (ticketButton) {
                        showTickets();
                        button_ticket.setBackgroundResource(R.drawable.cancel_circle_blue);
                        ticketButton = false;
                    } else {
                        for (int i = 0; i < createdAnnotations; i++) {
                            mapView.deleteAnnotation(i);
                        }
                        button_ticket.setBackgroundResource(R.drawable.ticket_circle_blue);
                        ticketButton = true;
                    }
                }
                break;
            case R.id.button_cancel_navigation:
                buttonCancelNavigation.setVisibility(View.GONE);
                imageViewSearch.setVisibility(View.VISIBLE);
                completeTextViewSearch.setVisibility(View.VISIBLE);
                button_menu.setVisibility(View.VISIBLE);
                button_search.setVisibility(View.VISIBLE);
                button_location.setVisibility(View.VISIBLE);
                button_bus.setVisibility(View.VISIBLE);
                button_ticket.setVisibility(View.VISIBLE);
                buttons = true;
                lockedUI = false;
                if (navigationStarted) {
                    SKRouteManager.getInstance().clearCurrentRoute();
                    SKNavigationManager.getInstance().stopNavigation();
                    mapView.deleteAnnotation(NAVIGATION_ANNOTATION);
                    refreshAnnotations();
                }
                break;
            case R.id.button_cancel_info_ticket_poi:
                imageViewTicketPoi.setVisibility(View.GONE);
                layoutTicketInfo.setVisibility(View.GONE);
                buttonCancelTicket.setVisibility(View.GONE);
                buttonRouteTicket.setVisibility(View.GONE);
                buttonInfoTicket.setVisibility(View.GONE);
                imageViewSearch.setVisibility(View.VISIBLE);
                completeTextViewSearch.setVisibility(View.VISIBLE);
                button_menu.setVisibility(View.VISIBLE);
                button_search.setVisibility(View.VISIBLE);
                button_location.setVisibility(View.VISIBLE);
                button_bus.setVisibility(View.VISIBLE);
                button_ticket.setVisibility(View.VISIBLE);
                buttons = true;
                lockedUI = false;
                break;
            case R.id.button_route_from_ticket_poi:
//                coordinateTicketPoi
                startNavigation(coordinateTicketPoi);
                imageViewSearch.setVisibility(View.GONE);
                completeTextViewSearch.setVisibility(View.GONE);
                button_menu.setVisibility(View.GONE);
                button_search.setVisibility(View.GONE);
                button_location.setVisibility(View.GONE);
                button_bus.setVisibility(View.GONE);
                button_ticket.setVisibility(View.GONE);
                imageViewTicketPoi.setVisibility(View.GONE);
                layoutTicketInfo.setVisibility(View.GONE);
                buttonCancelTicket.setVisibility(View.GONE);
                buttonRouteTicket.setVisibility(View.GONE);
                buttonInfoTicket.setVisibility(View.GONE);
                buttons = false;
                lockedUI = true;
                buttonCancelNavigation.setVisibility(View.VISIBLE);
                break;
            case R.id.button_more_info_ticket_poi:
                startActivity(new Intent(this, TicketsActivity.class));
                break;
            case R.id.button_cancel_info_station_poi:
                imageViewStationPoi.setVisibility(View.GONE);
                layoutStationInfo.setVisibility(View.GONE);
                buttonCancelStation.setVisibility(View.GONE);
                buttonRouteStation.setVisibility(View.GONE);
                buttonInfoStation.setVisibility(View.GONE);
                imageViewSearch.setVisibility(View.VISIBLE);
                completeTextViewSearch.setVisibility(View.VISIBLE);
                button_menu.setVisibility(View.VISIBLE);
                button_search.setVisibility(View.VISIBLE);
                button_location.setVisibility(View.VISIBLE);
                button_bus.setVisibility(View.VISIBLE);
                button_ticket.setVisibility(View.VISIBLE);
                buttons = true;
                lockedUI = false;
                break;
            case R.id.button_route_from_station_poi:
                startNavigation(coordinateStationPoi);
                imageViewSearch.setVisibility(View.GONE);
                completeTextViewSearch.setVisibility(View.GONE);
                button_menu.setVisibility(View.GONE);
                button_search.setVisibility(View.GONE);
                button_location.setVisibility(View.GONE);
                button_bus.setVisibility(View.GONE);
                button_ticket.setVisibility(View.GONE);
                imageViewStationPoi.setVisibility(View.GONE);
                layoutStationInfo.setVisibility(View.GONE);
                buttonCancelStation.setVisibility(View.GONE);
                buttonRouteStation.setVisibility(View.GONE);
                buttonInfoStation.setVisibility(View.GONE);
                buttons = false;
                lockedUI = true;
                buttonCancelNavigation.setVisibility(View.VISIBLE);
                break;
            case R.id.button_more_info_station_poi:
                startActivity(new Intent(this, StationsListActivity.class));
                break;
        }
    }

    private void createAnnotationPurple(SKCoordinate coordinate, int num) {
        SKAnnotation newAnnotation = new SKAnnotation(num);
        newAnnotation.setLocation(coordinate);
        newAnnotation.setMininumZoomLevel(5);
        newAnnotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_PURPLE);
        mapView.addAnnotation(newAnnotation, SKAnimationSettings.ANIMATION_PIN_DROP);
    }

    private void createAnnotationGreen(SKCoordinate coordinate, int num) {
        SKAnnotation newAnnotation = new SKAnnotation(num);
        newAnnotation.setLocation(coordinate);
        newAnnotation.setMininumZoomLevel(5);
        newAnnotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
        mapView.addAnnotation(newAnnotation, SKAnimationSettings.ANIMATION_PIN_DROP);
    }

    private void showTickets() {
        Cursor cursor = stationHelper.getInformation(stationHelper.getReadableDatabase());
        if (cursor.moveToFirst()) {
            int annotationNum;
            do {
                annotationNum = createdAnnotations;
                if (cursor.getString(5).equals("true")) {
                    createAnnotationGreen(new SKCoordinate(Double.parseDouble(cursor.getString(2)), Double.parseDouble(cursor.getString(3))), annotationNum);
                    createdAnnotations++;
                }
            } while (cursor.moveToNext());
        }
    }

    private void createAnnotationWithView(SKCoordinate coordinate) {

    }

    private void drawLine3() {
        SKPosition[] positions = {new SKPosition(23.628386, 46.767914), new SKPosition(23.622244, 46.768355), new SKPosition(23.616797, 46.769074), new SKPosition(23.608138, 46.769202),
                new SKPosition(23.599186, 46.768606), new SKPosition(23.597277, 46.771361), new SKPosition(23.592191, 46.771707), new SKPosition(23.587225, 46.777056),
                new SKPosition(23.588685, 46.784205), new SKPosition(23.591701, 46.780863), new SKPosition(23.591433, 46.774893), new SKPosition(23.596756, 46.770207),
                new SKPosition(23.602094, 46.767899), new SKPosition(23.610174, 46.769221), new SKPosition(23.616117, 46.769053), new SKPosition(23.619976, 46.768365),
                new SKPosition(23.624048, 46.768007), new SKPosition(23.628054, 46.767837)};

        SKRouteSettings settings = new SKRouteSettings();
        settings.setRouteMode(SKRouteSettings.SKRouteMode.BUS_FASTEST);
        SKRouteManager.getInstance().calculateRouteWithPoints(Arrays.asList(positions), settings);
    }

    private void drawTestLine() {
        SKPosition[] positions = {new SKPosition(23.628386, 46.767914), new SKPosition(23.622244, 46.768355)};
//        SKPosition position = new SKPosition();
        SKRouteSettings settings = new SKRouteSettings();
        settings.setRouteMode(SKRouteSettings.SKRouteMode.BUS_FASTEST);
        SKRouteManager.getInstance().calculateRouteWithPoints(Arrays.asList(positions), settings);
    }

    private void showAllLines() {
        Cursor cursor = stationHelper.getInformation(stationHelper.getReadableDatabase());
        if (cursor.moveToFirst()) {
            int annotationNum;
            do {
                annotationNum = createdAnnotations;
                createAnnotationPurple(new SKCoordinate(Double.parseDouble(cursor.getString(2)), Double.parseDouble(cursor.getString(3))), annotationNum);
                createdAnnotations ++;
            } while (cursor.moveToNext());
        }
    }

    public void startNavigation(SKCoordinate coordinate) {
        if (lastKnowCoordinates != null && created) {
            SKAnnotation annotationFinish = new SKAnnotation(NAVIGATION_ANNOTATION);
            annotationFinish.setLocation(coordinate);
            annotationFinish.setMininumZoomLevel(5);
            annotationFinish.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_DESTINATION_FLAG);
            launchRouteCalculation(lastKnowCoordinates, coordinate);
            mapView.addAnnotation(annotationFinish, SKAnimationSettings.ANIMATION_PIN_DROP);
//            mapView.deleteAllAnnotationsAndCustomPOIs();
//            button_bus.setBackgroundResource(R.drawable.bus_circle_blue);
//            busButton = true;
//            button_ticket.setBackgroundResource(R.drawable.ticket_circle_blue);
//            ticketButton = true;
//            mapView.addAnnotation(annotationFinish, SKAnimationSettings.ANIMATION_PIN_DROP);

            navigationStarted = true;
        } else if (currentPosition != null && created) {
            SKAnnotation annotationFinish = new SKAnnotation(NAVIGATION_ANNOTATION);
            annotationFinish.setLocation(coordinate);
            annotationFinish.setMininumZoomLevel(5);
            annotationFinish.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_DESTINATION_FLAG);
            mapView.addAnnotation(annotationFinish, SKAnimationSettings.ANIMATION_PIN_DROP);
            launchRouteCalculation(currentPosition.getCoordinate(), coordinate);
            navigationStarted = true;
        } else {
            Toast.makeText(this, "Please wait for the GPS to get your location", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentPosition != null) {
            lastKnowCoordinates = currentPosition.getCoordinate();
        }
        mapHolder.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapHolder.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentPositionProvider.stopLocationUpdates();
        SKMaps.getInstance().destroySKMaps();
    }

    @Override
    public void onBackPressed() {
        if (navigationStarted) {
            buttonCancelNavigation.setVisibility(View.GONE);
            imageViewSearch.setVisibility(View.VISIBLE);
            completeTextViewSearch.setVisibility(View.VISIBLE);
            button_menu.setVisibility(View.VISIBLE);
            button_search.setVisibility(View.VISIBLE);
            button_location.setVisibility(View.VISIBLE);
            button_bus.setVisibility(View.VISIBLE);
            button_ticket.setVisibility(View.VISIBLE);
            buttons = true;
            lockedUI = false;
            SKRouteManager.getInstance().clearCurrentRoute();
            SKNavigationManager.getInstance().stopNavigation();
            mapView.deleteAnnotation(NAVIGATION_ANNOTATION);
            refreshAnnotations();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActionPan() {

    }

    @Override
    public void onActionZoom() {

    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {
        mapView = mapHolder.getMapSurfaceView();
        if (currentPosition != null) {
            mapView.centerMapOnPosition(currentPosition.getCoordinate());
            Toast.makeText(this, "Connected to the GPS signal", Toast.LENGTH_LONG).show();
        } else {
            mapView.centerMapOnPosition(new SKCoordinate(23.593537, 46.773542));
            Toast.makeText(this, "GPS signal lost", Toast.LENGTH_LONG).show();
        }

        if (currentPosition != null) {
            SKPositionerManager.getInstance().reportNewGPSPosition(currentPosition);
            Toast.makeText(this, "Connected to the GPS signal", Toast.LENGTH_LONG).show();
        }

        imageViewSearch.setVisibility(View.VISIBLE);
        completeTextViewSearch.setVisibility(View.VISIBLE);
        button_menu.setVisibility(View.VISIBLE);
        button_search.setVisibility(View.VISIBLE);
        button_location.setVisibility(View.VISIBLE);
        button_bus.setVisibility(View.VISIBLE);
        button_ticket.setVisibility(View.VISIBLE);
        buttons = true;

        created = true;
    }

    @Override
    public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onDoubleTap(SKScreenPoint skScreenPoint) {
        System.out.println("Navigation Mode ------------- " + SKNavigationManager.getInstance().getNavigationMode());
        System.out.println("Simulation speed ------------ " + SKNavigationManager.getInstance().getCurrentSpeed());
    }

    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint) {
        if (!lockedUI) {
            if (buttons) {
                imageViewSearch.setVisibility(View.GONE);
                completeTextViewSearch.setVisibility(View.GONE);
                button_menu.setVisibility(View.GONE);
                button_search.setVisibility(View.GONE);
                button_location.setVisibility(View.GONE);
                button_bus.setVisibility(View.GONE);
                button_ticket.setVisibility(View.GONE);
                buttons = false;
            } else {
                imageViewSearch.setVisibility(View.VISIBLE);
                completeTextViewSearch.setVisibility(View.VISIBLE);
                button_menu.setVisibility(View.VISIBLE);
                button_search.setVisibility(View.VISIBLE);
                button_location.setVisibility(View.VISIBLE);
                button_bus.setVisibility(View.VISIBLE);
                button_ticket.setVisibility(View.VISIBLE);
                buttons = true;
            }
        }
    }

    @Override
    public void onRotateMap() {

    }

    @Override
    public void onLongPress(SKScreenPoint skScreenPoint) {
        skScreenPoint.getClass().getAnnotations();
    }

    @Override
    public void onInternetConnectionNeeded() {

    }

    @Override
    public void onMapActionDown(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onMapActionUp(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onPOIClusterSelected(SKPOICluster skpoiCluster) {

    }

    @Override
    public void onMapPOISelected(SKMapPOI skMapPOI) {

    }

    @Override
    public void onAnnotationSelected(SKAnnotation annotation) {

//        Intent intentTicket = new Intent(this, TicketsActivity.class);
//        Intent intentStation = new Intent(this, StationsListActivity.class);
        switch (annotation.getAnnotationType()) {
            case SKAnnotation.SK_ANNOTATION_TYPE_GREEN:
                imageViewTicketPoi.setVisibility(View.VISIBLE);
                layoutTicketInfo.setVisibility(View.VISIBLE);
                buttonCancelTicket.setVisibility(View.VISIBLE);
                buttonRouteTicket.setVisibility(View.VISIBLE);
                buttonInfoTicket.setVisibility(View.VISIBLE);
                imageViewSearch.setVisibility(View.GONE);
                completeTextViewSearch.setVisibility(View.GONE);
                button_menu.setVisibility(View.GONE);
                button_search.setVisibility(View.GONE);
                button_location.setVisibility(View.GONE);
                button_bus.setVisibility(View.GONE);
                button_ticket.setVisibility(View.GONE);
                buttons = false;
                lockedUI = true;
                mapView.centerMapOnPositionSmooth(annotation.getLocation(), 500);
                mapView.setZoomSmooth(500, 500);
                coordinateTicketPoi = annotation.getLocation();

                Cursor cursorTicket = stationHelper.getInformationForCoordinates(coordinateTicketPoi, stationHelper.getReadableDatabase());
                if (cursorTicket.moveToFirst()) {
                    TextView textStation = (TextView) findViewById(R.id.text_poi_station_ticket);
                    textStation.setText(cursorTicket.getString(0));
                    TextView textRoad = (TextView) findViewById(R.id.text_poi_road_ticket);
                    textRoad.setText(cursorTicket.getString(1));
                }
                break;
            case SKAnnotation.SK_ANNOTATION_TYPE_PURPLE:
                imageViewStationPoi.setVisibility(View.VISIBLE);
                layoutStationInfo.setVisibility(View.VISIBLE);
                buttonCancelStation.setVisibility(View.VISIBLE);
                buttonRouteStation.setVisibility(View.VISIBLE);
                buttonInfoStation.setVisibility(View.VISIBLE);
                imageViewSearch.setVisibility(View.GONE);
                completeTextViewSearch.setVisibility(View.GONE);
                button_menu.setVisibility(View.GONE);
                button_search.setVisibility(View.GONE);
                button_location.setVisibility(View.GONE);
                button_bus.setVisibility(View.GONE);
                button_ticket.setVisibility(View.GONE);
                buttons = false;
                lockedUI = true;
                mapView.centerMapOnPositionSmooth(annotation.getLocation(), 500);
                mapView.setZoomSmooth(500, 500);
                coordinateStationPoi = annotation.getLocation();
//                System.out.println("------------------ Aici ajunge (before Cursor) Coordinates: " + coordinateStationPoi);

                Cursor cursor = stationHelper.getInformationForCoordinates(coordinateStationPoi, stationHelper.getReadableDatabase());
                if (cursor.moveToFirst()) {
//                    System.out.println("------------------ Aici ajunge (in Cursor)");
                    TextView textStation = (TextView) findViewById(R.id.text_poi_station);
                    textStation.setText(cursor.getString(0));
                    TextView textRoad = (TextView) findViewById(R.id.text_poi_road);
                    textRoad.setText(cursor.getString(1));
                    String[] busLines = cursor.getString(4).split(" ");
                    HListView listBus = (HListView) findViewById(R.id.list_map_bus);
                    BusPoiItem[] busPoiItems = new BusPoiItem[busLines.length];
                    for (int i = 0; i < busLines.length; i++) {
                        cursor = busHelper.getInformationForLine(busLines[i], busHelper.getReadableDatabase());
                        if (cursor.moveToFirst()) {
                            if (cursor.getString(2).length() > 11) {
                                busPoiItems[i] = new BusPoiItem(Integer.parseInt(cursor.getString(1)), cursor.getString(0), cursor.getString(2).substring(0, 11));
//                                System.out.println("CITESTE");
                            } else {
                                busPoiItems[i] = new BusPoiItem(Integer.parseInt(cursor.getString(1)), cursor.getString(0), cursor.getString(2));
//                                System.out.println("CITESTE");
                            }
                        }
                    }
                    ArrayAdapter<BusPoiItem> busPoiAdapter = new BusPoiAdapter(this, busPoiItems);
                    listBus.setAdapter(busPoiAdapter);
//                    System.out.println("------------------ Aici ajunge (after setAdapter)");
                }

//                HListView listBus = (HListView) findViewById(R.id.list_map_bus);
//                BusPoiItem[] busPoiItems = new BusPoiItem[4];
//                busPoiItems[0] = new BusPoiItem(BusPoiItem.BusType.TROLLEY_BUS, "3", "21:09 21:18");
//                busPoiItems[1] = new BusPoiItem(BusPoiItem.BusType.TROLLEY_BUS, "25", "21:13 21:24");
//                busPoiItems[2] = new BusPoiItem(BusPoiItem.BusType.BUS, "34", "21:47 22:20");
//                busPoiItems[3] = new BusPoiItem(BusPoiItem.BusType.BUS, "48", "21:47 22:20");
//                ArrayAdapter<BusPoiItem> busPoiAdapter = new BusPoiAdapter(this, busPoiItems);
//                listBus.setAdapter(busPoiAdapter);
//                startActivity(intentStation);
                break;
        }
//        if (!annotationPopup) {
//            int annotationHeight = 0;
//            float annotationOffset = annotation.getOffset().getY();
//            switch (annotation.getUniqueID()) {
//                case 18:
//                    annotationHeight = (int) (64 * getResources().getDisplayMetrics().density);
//                    textAnnotation.setText("Annotation using texture ID");
//                    break;
////            case 11:
////                annotationHeight = customView.getHeight();
////                popupTitleView.setText("Annotation using custom view");
////                popupDescriptionView.setText(null);
////                break;
//            }
//            mapPopup.setVerticalOffset(-annotationOffset + annotationHeight / 2);
//            mapPopup.showAtLocation(annotation.getLocation(), true);
//            annotationPopup = true;
//        } else {
//            mapPopup.hide();
//        }
    }

    @Override
    public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI) {

    }

    @Override
    public void onCompassSelected() {

    }

    @Override
    public void onCurrentPositionSelected() {

    }

    @Override
    public void onObjectSelected(int i) {

    }

    @Override
    public void onInternationalisationCalled(int i) {

    }

    @Override
    public void onBoundingBoxImageRendered(int i) {

    }

    @Override
    public void onGLInitializationError(String s) {

    }

    @Override
    public void onScreenshotReady(Bitmap bitmap) {

    }

    @Override
    public void onCurrentPositionUpdate(SKPosition currentPosition) {
        this.currentPosition = currentPosition;
        SKPositionerManager.getInstance().reportNewGPSPosition(this.currentPosition);
//        if (skToolsNavigationInProgress) {
//            if (this.currentPosition.getHorizontalAccuracy() >= 150) {
//                numberOfConsecutiveBadPositionReceivedDuringNavi++;
//                if (numberOfConsecutiveBadPositionReceivedDuringNavi >= 3) {
//                    numberOfConsecutiveBadPositionReceivedDuringNavi = 0;
//                    onGPSSignalLost();
//                }
//            } else {
//                numberOfConsecutiveBadPositionReceivedDuringNavi = 0;
//                onGPSSignalRecovered();
//            }
//        }
    }

    private void launchRouteCalculation(SKCoordinate startPoint, SKCoordinate destinationPoint) {
        clearRouteFromCache();
        SKRouteSettings routeSettings = new SKRouteSettings();
        routeSettings.setStartCoordinate(startPoint);
        routeSettings.setDestinationCoordinate(destinationPoint);
        routeSettings.setNoOfRoutes(1);
        routeSettings.setRouteMode(SKRouteSettings.SKRouteMode.PEDESTRIAN);
        routeSettings.setRouteExposed(true);
        SKRouteManager.getInstance().setRouteListener(this);
        SKRouteManager.getInstance().calculateRoute(routeSettings);
    }

    public void clearRouteFromCache() {
        SKRouteManager.getInstance().clearAllRoutesFromCache();
    }

    @Override
    public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo) {
        // TODO: 10-Jun-16 skRouteInfo.getEstimatedTime()
        System.out.println("--------------------------- MapActivity.onRouteCalculationCompleted");
    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {

    }

    @Override
    public void onAllRoutesCompleted() {
        SKNavigationSettings navigationSettings = new SKNavigationSettings();
        navigationSettings.setNavigationType(SKNavigationSettings.SKNavigationType.REAL);
        SKNavigationManager.getInstance().decreaseSimulationSpeed(15);
        SKNavigationManager navigationManager = SKNavigationManager.getInstance();
        navigationManager.setMapView(mapView);
        navigationManager.setNavigationListener(this);
        navigationManager.startNavigation(navigationSettings);
    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {

    }

    @Override
    public void onOnlineRouteComputationHanging(int i) {

    }

    @Override
    public void onDestinationReached() {
        Toast.makeText(MapActivity.this, "Destination reached", Toast.LENGTH_SHORT).show();
        SKRouteManager.getInstance().clearCurrentRoute();
        SKNavigationManager.getInstance().stopNavigation();
        mapView.deleteAnnotation(NAVIGATION_ANNOTATION);
    }

    @Override
    public void onSignalNewAdviceWithInstruction(String s) {

    }

    @Override
    public void onSignalNewAdviceWithAudioFiles(String[] strings, boolean b) {

    }

    @Override
    public void onSpeedExceededWithAudioFiles(String[] strings, boolean b) {

    }

    @Override
    public void onSpeedExceededWithInstruction(String s, boolean b) {

    }

    @Override
    public void onUpdateNavigationState(SKNavigationState skNavigationState) {

    }

    @Override
    public void onReRoutingStarted() {

    }

    @Override
    public void onFreeDriveUpdated(String s, String s1, String s2, SKNavigationState.SKStreetType skStreetType, double v, double v1) {

    }

    @Override
    public void onViaPointReached(int i) {

    }

    @Override
    public void onVisualAdviceChanged(boolean b, boolean b1, SKNavigationState skNavigationState) {

    }

    @Override
    public void onTunnelEvent(boolean b) {

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && getApplicationContext().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //   gps functions.
        }
    }

    private void refreshAnnotations() {
        mapView.deleteAllAnnotationsAndCustomPOIs();
        button_bus.setBackgroundResource(R.drawable.bus_circle_blue);
        busButton = true;
        button_ticket.setBackgroundResource(R.drawable.ticket_circle_blue);
        ticketButton = true;
    }

    private void showLine(String line) {
        stationHelper = new StationHelper(this);
        busHelper = new BusHelper(this);
        int annotationNum;
        String[] stations;
        if (busHelper.isLine(line)) {
            Cursor cursorBus = busHelper.getInformationForLine(line, busHelper.getReadableDatabase());
            if (cursorBus.moveToFirst()) {
                if (cursorBus.getString(3).equals("null")) {
                    return;
                } else {
                    stations = cursorBus.getString(3).split(";");
                    Cursor cursorStation;
                    for (int i = 0; i < stations.length; i++) {
                        cursorStation = stationHelper.getInformationForStation(stations[i], stationHelper.getReadableDatabase());
                        if (cursorStation.moveToFirst()) {
                            annotationNum = createdAnnotations;
                            createAnnotationPurple(new SKCoordinate(Double.parseDouble(cursorStation.getString(2)), Double.parseDouble(cursorStation.getString(3))), annotationNum);
                            createdAnnotations++;
                        }
                    }
                }
            } else {
                return;
            }
        }
    }
}