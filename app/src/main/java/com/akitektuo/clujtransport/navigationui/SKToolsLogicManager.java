package com.akitektuo.clujtransport.navigationui;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSettings;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKMapViewStyle;
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
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKRouteAdvice;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.routing.SKViaPoint;
import com.akitektuo.clujtransport.navigationui.autonight.SKToolsAutoNightManager;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.trail.SKTrailType;
import com.skobbler.ngx.util.SKLogging;

/**
 * This class handles the logic related to the navigation and route calculation.
 */
public class SKToolsLogicManager implements SKMapSurfaceListener, SKRouteListener,
        SKCurrentPositionListener {

    /**
     * Singleton instance for current class
     */
    private static volatile SKToolsLogicManager instance = null;

    /**
     * the map view instance
     */
    private SKMapSurfaceView mapView;

    /**
     * the view that holds the map view
     */
    private SKMapViewHolder mapHolder;

    /**
     * the current activity
     */
    private Activity currentActivity;

    /**
     * Current position provider
     */
    private SKCurrentPositionProvider currentPositionProvider;

    /**
     * Navigation manager
     */
    private SKNavigationManager naviManager;

    /**
     * the initial configuration for calculating route and navigating
     */
    private SKToolsNavigationConfiguration configuration;

    /**
     * number of options pressed (in navigation settings).
     */
    public int numberOfSettingsOptionsPressed;

    /**
     * last audio advices that needs to be played when the visual advice is
     * pressed
     */
    private String[] lastAudioAdvices;

    /**
     * the distance left to the destination after every route update
     */
    private long navigationCurrentDistance;

    /**
     * boolean value which shows if there are blocks on the route or not
     */
    private boolean roadBlocked;

    /**
     * flag for when a re-routing was done, which is set to true only until the
     * next update of the navigation state
     */
    private boolean reRoutingInProgress = false;

    /**
     * the current location
     */
    public static volatile SKPosition lastUserPosition;

    /**
     * true, if the navigation was stopped
     */
    private boolean navigationStopped;

    /**
     * Map surface listener
     */
    private SKMapSurfaceListener previousMapSurfaceListener;

    /**
     * SKRouteInfo list
     */
    private List<SKRouteInfo> skRouteInfoList = new ArrayList<SKRouteInfo>();

    /**
     * Navigation listener
     */
    private SKToolsNavigationListener navigationListener;

    /*
    Current map style
     */
    private SKMapViewStyle currentMapStyle;

    /*
    Current display mode
     */
    private SKMapSettings.SKMapDisplayMode currentUserDisplayMode;
    /**
     * start pedestrian navigation
     */
    public boolean startPedestrian=false;

    public static SKToolsLogicManager getInstance() {
        if (instance == null) {
            synchronized (SKToolsLogicManager.class) {
                if (instance == null) {
                    instance = new SKToolsLogicManager();
                }
            }
        }
        return instance;
    }

    private SKToolsLogicManager() {
        naviManager = SKNavigationManager.getInstance();
    }

    /**
     * Sets the current activity.
     *
     * @param activity
     * @param rootId
     */
    protected void setActivity(Activity activity, int rootId) {
        this.currentActivity = activity;
        currentPositionProvider = new SKCurrentPositionProvider(currentActivity);
        if (SKToolsUtils.hasGpsModule(currentActivity)) {
            currentPositionProvider.requestLocationUpdates(true, false, true);
        } else if (SKToolsUtils.hasNetworkModule(currentActivity)) {
            currentPositionProvider.requestLocationUpdates(false, true, true);
        }
        currentPositionProvider.setCurrentPositionListener(this);


    }

    /**
     * Sets the listener.
     *
     * @param navigationListener
     */
    public void setNavigationListener(SKToolsNavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }

    /**
     * Starts a route calculation.
     *
     * @param configuration
     * @param mapHolder
     */
    protected void calculateRoute(SKToolsNavigationConfiguration configuration, SKMapViewHolder mapHolder) {
        this.mapHolder = mapHolder;
        this.mapView = mapHolder.getMapSurfaceView();
        this.configuration = configuration;
        SKToolsMapOperationsManager.getInstance().setMapView(mapView);
        currentPositionProvider.requestUpdateFromLastPosition();
        currentMapStyle = mapView.getMapSettings().getMapStyle();
        SKRouteSettings route = new SKRouteSettings();
        route.setStartCoordinate(configuration.getStartCoordinate());
        route.setDestinationCoordinate(configuration.getDestinationCoordinate());
        SKToolsMapOperationsManager.getInstance().drawDestinationNavigationFlag(configuration
                        .getDestinationCoordinate().getLongitude(),
                configuration.getDestinationCoordinate().getLatitude());
        List<SKViaPoint> viaPointList;
        viaPointList = configuration.getViaPointCoordinateList();
        if (viaPointList != null) {
            route.setViaPoints(viaPointList);
        }

        if (configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN) {
            SKRouteManager.getInstance().enablePedestrianTrail(true, 5);
        }
        if (configuration.getRouteType() == SKRouteSettings.SKRouteMode.CAR_SHORTEST) {
            route.setNoOfRoutes(1);
        } else {
            route.setNoOfRoutes(3);
        }

        route.setRouteMode(configuration.getRouteType());
        route.setRouteExposed(true);
        route.setTollRoadsAvoided(configuration.isTollRoadsAvoided());
        route.setAvoidFerries(configuration.isFerriesAvoided());
        route.setHighWaysAvoided(configuration.isHighWaysAvoided());
        SKRouteManager.getInstance().setRouteListener(this);

        SKRouteManager.getInstance().calculateRoute(route);

        if (configuration.isAutomaticDayNight() && lastUserPosition != null) {
            SKToolsAutoNightManager.getInstance().setAutoNightAlarmAccordingToUserPosition(lastUserPosition.getCoordinate(), currentActivity);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SKToolsAutoNightManager.getInstance().setAlarmForHourlyNotificationAfterKitKat(currentActivity, true);
            } else {
                SKToolsAutoNightManager.getInstance().setAlarmForHourlyNotification(currentActivity);
            }

            checkCorrectMapStyle();
        }
        navigationStopped = false;

        if (navigationListener != null) {
            navigationListener.onRouteCalculationStarted();
        }
    }


    /**
     * Starts a navigation with the specified configuration.
     *
     * @param configuration
     * @param mapHolder
     * @param isFreeDrive
     */
    protected void startNavigation(SKToolsNavigationConfiguration configuration,
                                   SKMapViewHolder mapHolder, boolean isFreeDrive) {

        SKNavigationSettings navigationSettings = new SKNavigationSettings();
        reRoutingInProgress = false;
        this.configuration = configuration;
        this.mapHolder = mapHolder;
        mapView = mapHolder.getMapSurfaceView();
        if (configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN) {
            currentUserDisplayMode = SKMapSettings.SKMapDisplayMode.MODE_2D;
            Toast.makeText(this.currentActivity, "The map will turn based on your recent positions.", Toast.LENGTH_SHORT).show();
            mapView.getMapSettings().setFollowerMode(SKMapSettings.SKMapFollowerMode.HISTORIC_POSITION);
            SKTrailType trailType = new SKTrailType();
            trailType.setPedestrianTrailEnabled(true, 1);
            navigationSettings.setTrailType(trailType);
            navigationSettings.setCcpAsCurrentPosition(true);
            mapView.getMapSettings().setCompassPosition(new SKScreenPoint(10, 70));
            mapView.getMapSettings().setCompassShown(true);
            navigationSettings.setNavigationMode(SKNavigationSettings.SKNavigationMode.PEDESTRIAN);
            startPedestrian=true;
        } else {
            currentUserDisplayMode = SKMapSettings.SKMapDisplayMode.MODE_3D;
            mapView.getMapSettings().setFollowerMode(SKMapSettings.SKMapFollowerMode.NAVIGATION);
        }
        mapView.getMapSettings().setMapDisplayMode(currentUserDisplayMode);
        mapView.getMapSettings().setStreetNamePopupsShown(true);
        mapView.getMapSettings().setMapZoomingEnabled(false);
        previousMapSurfaceListener = mapView.getMapSurfaceListener();
        mapHolder.setMapSurfaceListener(this);
        SKToolsMapOperationsManager.getInstance().setMapView(mapView);

        navigationSettings.setNavigationType(configuration.getNavigationType());
        navigationSettings.setPositionerVerticalAlignment(-0.25f);
        navigationSettings.setShowRealGPSPositions(false);
        navigationSettings.setDistanceUnit(configuration.getDistanceUnitType());
        navigationSettings.setSpeedWarningThresholdInCity(configuration.getSpeedWarningThresholdInCity());
        navigationSettings.setSpeedWarningThresholdOutsideCity(configuration.getSpeedWarningThresholdOutsideCity());
        if (configuration.getNavigationType().equals(SKNavigationSettings.SKNavigationType.FILE)) {
            navigationSettings.setFileNavigationPath(configuration.getFreeDriveNavigationFilePath());
        }
        naviManager.setMapView(mapView);
        naviManager.startNavigation(navigationSettings);

        if (configuration.getNavigationType() == SKNavigationSettings.SKNavigationType.SIMULATION) {
        }
        if (isFreeDrive) {
            currentMapStyle = mapView.getMapSettings().getMapStyle();
        }
        currentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d("", "lastUserPosition = " + lastUserPosition);
        if (configuration.isAutomaticDayNight() && lastUserPosition != null) {
            if (isFreeDrive) {
                SKToolsAutoNightManager.getInstance().setAutoNightAlarmAccordingToUserPosition(lastUserPosition.getCoordinate(), currentActivity);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    SKToolsAutoNightManager.getInstance().setAlarmForHourlyNotificationAfterKitKat(currentActivity,
                            true);
                } else {
                    SKToolsAutoNightManager.getInstance().setAlarmForHourlyNotification(currentActivity);
                }

                checkCorrectMapStyle();
            }
        }

        navigationStopped = false;

        if (navigationListener != null) {
            navigationListener.onNavigationStarted();
        }
    }

    /**
     * Stops the navigation.
     */
    protected void stopNavigation() {
        SKToolsMapOperationsManager.getInstance().startPanningMode();
        mapView.getMapSettings().setMapStyle(currentMapStyle);
        mapView.getMapSettings().setCompassShown(false);
        SKRouteManager.getInstance().clearCurrentRoute();
        naviManager.stopNavigation();
        currentPositionProvider.stopLocationUpdates();
        mapHolder.setMapSurfaceListener(previousMapSurfaceListener);
        mapView.rotateTheMapToNorth();
        navigationStopped = true;
        startPedestrian=false;
        if (configuration.getDestinationCoordinate() != null) {
            SKToolsMapOperationsManager.getInstance().drawDestinationPoint(configuration.getDestinationCoordinate().getLongitude(), configuration.getDestinationCoordinate().getLatitude());
        }
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });

        if (navigationListener != null) {
            navigationListener.onNavigationEnded();
        }

        SKToolsAdvicePlayer.getInstance().stop();
    }


    /**
     * Checks the correct map style, taking into consideration auto night configuration settings.
     */
    private void checkCorrectMapStyle() {
        int currentMapStyle = SKToolsMapOperationsManager.getInstance().getCurrentMapStyle();
        int correctMapStyle = SKToolsMapOperationsManager.getInstance().getMapStyleBeforeStartDriveMode(configuration
                .isAutomaticDayNight());
        if (currentMapStyle != correctMapStyle) {
            SKToolsMapOperationsManager.getInstance().switchDayNightStyle(configuration, correctMapStyle);
        }
    }

    /**
     * Checks if the navigation is stopped.
     *
     * @return
     */
    public boolean isNavigationStopped() {
        return navigationStopped;
    }

    /**
     * Gets the current activity.
     *
     * @return
     */
    public Activity getCurrentActivity() {
        return currentActivity;
    }

    /**
     * Handles orientation changed.
     */
    public void notifyOrientationChanged() {
        int mapStyle = SKToolsMapOperationsManager.getInstance().getCurrentMapStyle();
        SKMapSettings.SKMapDisplayMode displayMode = mapView.getMapSettings().getMapDisplayMode();
    }

    /**
     * Removes the pre navigation screen.
     */
    protected void removeRouteCalculationScreen() {
        SKRouteManager.getInstance().clearCurrentRoute();
        SKRouteManager.getInstance().clearRouteAlternatives();
        skRouteInfoList.clear();
        System.out.println("------ current map style remove" + mapView.getMapSettings().getMapStyle());
        mapView.getMapSettings().setMapStyle(currentMapStyle);
        SKToolsAutoNightManager.getInstance().cancelAlarmForForHourlyNotification();
        SKToolsMapOperationsManager.getInstance().drawDestinationPoint(configuration.getDestinationCoordinate().getLongitude(), configuration.getDestinationCoordinate().getLatitude());
        if (navigationListener != null) {
            navigationListener.onRouteCalculationCanceled();
        }
    }

    /**
     * play the last advice
     */
    protected void playLastAdvice() {
        if (!(configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN)) {
            SKToolsAdvicePlayer.getInstance().playAdvice(lastAudioAdvices, SKToolsAdvicePlayer.PRIORITY_USER);
        }
    }

    /**
     * Checks if the roads are blocked.
     *
     * @return
     */
    protected boolean isRoadBlocked() {
        return roadBlocked;
    }

    /**
     * Changes the map style from day -> night or night-> day
     */
    private void loadDayNightSettings(SKToolsNavigationConfiguration configuration) {
        int mapStyle = SKToolsMapOperationsManager.getInstance().getCurrentMapStyle();
        int newStyle;
        if (mapStyle == SKToolsMapOperationsManager.DAY_STYLE) {
            newStyle = SKToolsMapOperationsManager.NIGHT_STYLE;
        } else {
            newStyle = SKToolsMapOperationsManager.DAY_STYLE;
        }

        SKToolsMapOperationsManager.getInstance().switchDayNightStyle(configuration, newStyle);
    }


    /**
     * Decides the style in which the map needs to be changed next.
     */
    public void computeMapStyle(boolean isDaytime) {
        Log.d("", "Update the map style after receiving the broadcast");
        int mapStyle;
        if (isDaytime) {
            mapStyle = SKToolsMapOperationsManager.DAY_STYLE;
        } else {
            mapStyle = SKToolsMapOperationsManager.NIGHT_STYLE;
        }
        SKToolsMapOperationsManager.getInstance().switchDayNightStyle(configuration, mapStyle);
    }


    /**
     * Changes the map display from 3d-> 2d and vice versa
     */
    private void loadMapDisplayMode() {
        SKMapSettings.SKMapDisplayMode displayMode = mapView.getMapSettings().getMapDisplayMode();
        SKMapSettings.SKMapDisplayMode newDisplayMode;
        if (displayMode == SKMapSettings.SKMapDisplayMode.MODE_3D) {
            newDisplayMode = SKMapSettings.SKMapDisplayMode.MODE_2D;
        } else {
            newDisplayMode = SKMapSettings.SKMapDisplayMode.MODE_3D;
        }

        currentUserDisplayMode = newDisplayMode;
        SKToolsMapOperationsManager.getInstance().switchMapDisplayMode(newDisplayMode);
    }


    @Override
    public void onActionPan() {

    }

    @Override
    public void onActionZoom() {
        float currentZoom = mapView.getZoomLevel();
        if (currentZoom < 5) {
            // do not show the blue dot
            mapView.getMapSettings().setCurrentPositionShown(false);
        } else {
            mapView.getMapSettings().setCurrentPositionShown(true);
        }
    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder holder) {
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
    }

    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onRotateMap() {
    }

    @Override
    public void onLongPress(SKScreenPoint skScreenPoint) {
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
    public void onAnnotationSelected(SKAnnotation skAnnotation) {
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
    public void onGLInitializationError(String messsage) {

    }

    @Override
    public void onScreenshotReady(Bitmap bitmap) {

    }

    public void onDestinationReached() {

    }

    public void onSignalNewAdviceWithInstruction(String instruction) {
    }

    public void onSignalNewAdviceWithAudioFiles(String[] audioFiles, boolean specialSoundFile) {
        if (!(configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN)) {
            SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);
        }
    }

    public void onSpeedExceededWithAudioFiles(String[] adviceList, boolean speedExceeded) {
        if (!(configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN)) {
            playSoundWhenSpeedIsExceeded(adviceList, speedExceeded);
        }
    }

    /**
     * play sound when the speed is exceeded
     *
     * @param adviceList    - the advices that needs to be played
     * @param speedExceeded - true if speed is exceeded, false otherwise
     */
    private void playSoundWhenSpeedIsExceeded(final String[] adviceList, final boolean speedExceeded) {
        if (!navigationStopped) {
            currentActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (speedExceeded) {
                        SKToolsAdvicePlayer.getInstance()
                                .playAdvice(adviceList, SKToolsAdvicePlayer.PRIORITY_SPEED_WARNING);
                    }
                }
            });
        }
    }

    public void onSpeedExceededWithInstruction(String instruction, boolean speedExceeded) {
    }

    public void onTunnelEvent(boolean b) {
    }

    @Override
    public void onRouteCalculationCompleted(final SKRouteInfo skRouteInfo) {
        if (!skRouteInfo.isCorridorDownloaded()) {
            return;
        }
        skRouteInfoList.add(skRouteInfo);
        if (configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN) {
            SKRouteManager.getInstance().renderRouteAsPedestrian(skRouteInfo.getRouteID());
        }


        final List<SKRouteAdvice> advices = SKRouteManager.getInstance().getAdviceList(skRouteInfo.getRouteID(), SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS);
        if (advices != null){
            for (SKRouteAdvice advice : advices){
                SKLogging.writeLog("SKToolsLogicManager", " Route advice is " + advice.toString(), SKLogging.LOG_DEBUG);
            }
        }

        final String[] routeSummary = skRouteInfo.getRouteSummary();
        if (routeSummary != null){
            for( String street :routeSummary){
                SKLogging.writeLog("SKToolsLogicManager" , " Route Summary street = " + street , SKLogging.LOG_ERROR);
            }
        }else{
            SKLogging.writeLog("SKToolsLogicManager", "Route summary is null " , SKLogging.LOG_ERROR);
        }
    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {

    }

    @Override
    public void onAllRoutesCompleted() {

    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {
    }

    @Override
    public void onOnlineRouteComputationHanging(int i) {
    }

    @Override
    public void onCurrentPositionUpdate(SKPosition skPosition) {
        lastUserPosition = skPosition;
        if (configuration.getNavigationType() == SKNavigationSettings.SKNavigationType.REAL) {
            SKPositionerManager.getInstance().reportNewGPSPosition(skPosition);
        }
    }

}
