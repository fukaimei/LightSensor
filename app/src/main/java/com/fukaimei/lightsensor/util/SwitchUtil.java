package com.fukaimei.lightsensor.util;

import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class SwitchUtil {
    private static final String TAG = "SwitchUtil";

    // 获取Gps的开关状态
    public static boolean getGpsStatus(Context ctx) {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gps_enabled;
    }

    // 打开或关闭Gps
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setGpsStatus(Context ctx, boolean enabled) {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(ctx, 0, gpsIntent, 0).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取定位的开关状态
    public static boolean getLocationStatus(Context ctx) {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps_enabled || network_enabled;
    }

    // 获取Wifi的开关状态
    public static boolean getWlanStatus(Context ctx) {
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        return wm.isWifiEnabled();
    }

    // 打开或关闭Wifi
    public static void setWlanStatus(Context ctx, boolean enabled) {
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(enabled);
    }

    // 获取数据连接的开关状态
    public static boolean getMobileDataStatus(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isOpen = false;
        try {
            String methodName = "getMobileDataEnabled";
            Method method = cm.getClass().getMethod(methodName);
            isOpen = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    // 打开或关闭数据连接
    public static void setMobileDataStatus(Context ctx, boolean enabled) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            String methodName = "setMobileDataEnabled";
            Method method = cm.getClass().getMethod(methodName, Boolean.TYPE);
            // method.setAccessible(true);
            method.invoke(cm, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置亮度自动调节的开关
    public static void setAutoBrightStatus(Context ctx, boolean enabled) {
        int screenMode = (enabled == true) ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
        Settings.System.putInt(ctx.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, screenMode);
    }

    // 获取亮度自动调节的状态
    public static boolean getAutoBrightStatus(Context ctx) {
        int screenMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
        try {
            screenMode = Settings.System.getInt(ctx.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception e) {
            Log.d(TAG, "getAutoBrightStatus error: " + e.getMessage());
        }
        return screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ? true : false;
    }

    // Camera对象需要做成单例模式，因为Camera不能重复打开
    private static Camera mCamera = null;

    // 获取闪光灯/手电筒的开关状态
    public static boolean getFlashStatus(Context ctx) {
        if (mCamera == null) {
            mCamera = Camera.open();
        }
        Parameters parameters = mCamera.getParameters();
        String flashMode = parameters.getFlashMode();
        boolean enabled;
        if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {
            enabled = true;
        } else {
            enabled = false;
        }
        return enabled;
    }

    // 打开或关闭闪光灯/手电筒
    public static void setFlashStatus(Context ctx, boolean enabled) {
        if (mCamera == null) {
            mCamera = Camera.open();
        }
        Parameters parameters = mCamera.getParameters();
        if (enabled == true) {
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
            mCamera.setParameters(parameters);
        } else {
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
            mCamera.setParameters(parameters);
            mCamera.release();
            mCamera = null;
        }
    }

}
