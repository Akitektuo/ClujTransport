package com.akitektuo.clujtransport.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.location.LocationManager;

import com.skobbler.ngx.SKDeveloperKeyException;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitSettings;
import com.skobbler.ngx.map.SKMapViewStyle;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.util.SKLogging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AppUtils {

    public static boolean isMultipleMapSupportEnabled;

    public static boolean hasGpsModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasNetworkModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                return true;
            }
        }
        return false;
    }

    public static boolean initializeLibrary(final Activity context) {
        SKLogging.enableLogs(true);

        // get object holding map initialization settings
        SKMapsInitSettings initMapSettings = new SKMapsInitSettings();

//        final String  mapResourcesPath = ((MapApplication)context.getApplicationContext()).getAppPrefs().getStringPreference("mapResourcesPath");
        // set path to map resources and initial map style
//        initMapSettings.setMapResourcesPaths(mapResourcesPath,
//                new SKMapViewStyle(mapResourcesPath + "daystyle/", "daystyle.json"));

        final SKAdvisorSettings advisorSettings = initMapSettings.getAdvisorSettings();
//        advisorSettings.setAdvisorConfigPath(mapResourcesPath +"/Advisor");
//        advisorSettings.setResourcePath(mapResourcesPath +"/Advisor/Languages");
        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
        advisorSettings.setAdvisorVoice("en");
        initMapSettings.setAdvisorSettings(advisorSettings);

        // EXAMPLE OF ADDING PREINSTALLED MAPS
//         initMapSettings.setPreinstalledMapsPath(((DemoApplication)context.getApplicationContext()).getMapResourcesDirPath()
//         + "/PreinstalledMaps");
        // initMapSettings.setConnectivityMode(SKMaps.CONNECTIVITY_MODE_OFFLINE);

        // Example of setting light maps
        // initMapSettings.setMapDetailLevel(SKMapsInitSettings.SK_MAP_DETAIL_LIGHT);
        // initialize map using the settings object

        try {
            SKMaps.getInstance().initializeSKMaps(context, initMapSettings);
            return true;
        }catch (SKDeveloperKeyException exception){
            exception.printStackTrace();
//            showApiKeyErrorDialog(context);
            return false;
        }
    }

    public static void copyAssetsToFolder(AssetManager assetManager, String sourceFolder, String destinationFolder)
            throws IOException {
        final String[] assets = assetManager.list(sourceFolder);

        final File destFolderFile = new File(destinationFolder);
        if (!destFolderFile.exists()) {
            destFolderFile.mkdirs();
        }
        copyAsset(assetManager, sourceFolder, destinationFolder, assets);
    }

    public static void copyAsset(AssetManager assetManager, String sourceFolder, String destinationFolder,
                                 String... assetsNames) throws IOException {

        for (String assetName : assetsNames) {
            OutputStream destinationStream = new FileOutputStream(new File(destinationFolder + "/" + assetName));
            String[] files = assetManager.list(sourceFolder + "/" + assetName);
            if (files == null || files.length == 0) {

//                InputStream asset = assetManager.open(sourceFolder + "/" + assetName);
//                try {
//                    copy(asset, destinationStream);
//                } finally {
//                    asset.close();
//                    destinationStream.close();
//                }
            }
        }
    }

    public static void deleteFileOrDirectory(File file){
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                if(new File(file,children[i]).isDirectory() &&!children[i].equals("PreinstalledMaps") &&!children[i].equals("Maps")){
                    deleteFileOrDirectory(new File(file,children[i]));
                }else{
                    new File(file,children[i]).delete();
                }
            }
        }
    }
}