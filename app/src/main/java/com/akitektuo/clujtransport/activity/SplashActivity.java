package com.akitektuo.clujtransport.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.akitektuo.clujtransport.R;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitSettings;
import com.skobbler.ngx.SKPrepareMapTextureListener;
import com.skobbler.ngx.SKPrepareMapTextureThread;
import com.skobbler.ngx.map.SKMapViewStyle;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.ngx.versioning.SKVersioningManager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SplashActivity extends Activity implements SKPrepareMapTextureListener {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private static final String TAG = "SplashActivity";

    public static final long KILO = 1024;

    public static final long MEGA = KILO * KILO;

    public static String mapResourcesDirPath = "";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
//        String applicationPath = chooseStoragePath(this);
//        if (applicationPath != null) {
//            mapResourcesDirPath = applicationPath + "/" + "SKMaps/";
//        }
//        if (!new File(mapResourcesDirPath).exists()) {
//            // copy some other resource needed
//            new SKPrepareMapTextureThread(this, mapResourcesDirPath, "SKMaps.zip", this).start();
//        }


//        Environment.getExternalStorageDirectory()
        marshmallowGPSPermissionCheck();
        String applicationPath = chooseStoragePath(this);
        mapResourcesDirPath = applicationPath + "/" + "SKMaps/";
        final SKPrepareMapTextureThread prepThread = new SKPrepareMapTextureThread(this, mapResourcesDirPath, "SKMaps.zip", this);
        prepThread.start();
        System.out.println("SplashActivity.onCreate");
    }

    private void marshmallowGPSPermissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getApplicationContext().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //   gps functions.
        }
    }

    @Override
    public void onMapTexturesPrepared(boolean prepared) {
        if (prepared) {

            SKMapsInitSettings skMapsInitSettings = new SKMapsInitSettings();
            String mapResourcePath = mapResourcesDirPath;
            skMapsInitSettings.setMapResourcesPaths(mapResourcePath, new SKMapViewStyle(mapResourcePath + "daystyle/", "daystyle.json"));
            SKAdvisorSettings skAdvisorSettings = new SKAdvisorSettings();
            skAdvisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
            skAdvisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.TEXT_TO_SPEECH);
            skAdvisorSettings.setAdvisorConfigPath(mapResourcePath + "/Advisor");
            skAdvisorSettings.setResourcePath(mapResourcePath + "/Advisor/Languages");
            skMapsInitSettings.setAdvisorSettings(skAdvisorSettings);

            SKMaps.getInstance().initializeSKMaps(this, skMapsInitSettings);

            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MapActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    System.out.println("SplashActivity.run");
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    public static String chooseStoragePath(Context context) {
        if (getAvailableMemorySize(Environment.getDataDirectory().getPath()) >= 50 * MEGA) {
            if (context != null && context.getFilesDir() != null) {
                return context.getFilesDir().getPath();
            }
        } else {
            if ((context != null) && (context.getExternalFilesDir(null) != null)) {
                if (getAvailableMemorySize(context.getExternalFilesDir(null).toString()) >= 50 * MEGA) {
                    return context.getExternalFilesDir(null).toString();
                }
            }
        }

        SKLogging.writeLog(TAG, "There is not enough memory on any storage, but return internal memory",
                SKLogging.LOG_DEBUG);

        if (context != null && context.getFilesDir() != null) {
            return context.getFilesDir().getPath();
        } else {
            if ((context != null) && (context.getExternalFilesDir(null) != null)) {
                return context.getExternalFilesDir(null).toString();
            } else {
                return null;
            }
        }
    }

    public static long getAvailableMemorySize(String path) {
        StatFs statFs = null;
        try {
            statFs = new StatFs(path);
        } catch (IllegalArgumentException ex) {
            SKLogging.writeLog("SplashActivity", "Exception when creating StatF ; message = " + ex,
                    SKLogging.LOG_DEBUG);
        }
        if (statFs != null) {
            Method getAvailableBytesMethod = null;
            try {
                getAvailableBytesMethod = statFs.getClass().getMethod("getAvailableBytes");
            } catch (NoSuchMethodException e) {
                SKLogging.writeLog(TAG, "Exception at getAvailableMemorySize method = " + e.getMessage(),
                        SKLogging.LOG_DEBUG);
            }

            if (getAvailableBytesMethod != null) {
                try {
                    SKLogging.writeLog(TAG, "Using new API for getAvailableMemorySize method !!!", SKLogging.LOG_DEBUG);
                    return (Long) getAvailableBytesMethod.invoke(statFs);
                } catch (IllegalAccessException e) {
                    return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
                } catch (InvocationTargetException e) {
                    return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
                }
            } else {
                return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
            }
        } else {
            return 0;
        }
    }
}
