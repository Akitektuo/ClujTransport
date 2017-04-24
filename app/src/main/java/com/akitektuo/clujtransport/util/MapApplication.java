package com.akitektuo.clujtransport.util;

import android.app.Application;

/**
 * Created by AoD Akitektuo on 09-Jun-16.
 */
public class MapApplication extends Application {

    private String mapResourcesDirPath;

    /**
     * Absolute path to the file used for mapCreator - mapcreatorFile.json
     */
    private String mapCreatorFilePath;

    /**
     * Object for accessing application preferences
     */
//    private ApplicationPreferences appPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
//        appPrefs = new ApplicationPreferences(this);
    }

    public void setMapResourcesDirPath(String mapResourcesDirPath) {
        this.mapResourcesDirPath = mapResourcesDirPath;
    }

    public String getMapResourcesDirPath() {
        return mapResourcesDirPath;
    }

    public String getMapCreatorFilePath() {
        return mapCreatorFilePath;
    }

    public void setMapCreatorFilePath(String mapCreatorFilePath) {
        this.mapCreatorFilePath = mapCreatorFilePath;
    }

//    public ApplicationPreferences getAppPrefs() {
//        return appPrefs;
//    }
//
//    public void setAppPrefs(ApplicationPreferences appPrefs) {
//        this.appPrefs = appPrefs;
//    }

}
