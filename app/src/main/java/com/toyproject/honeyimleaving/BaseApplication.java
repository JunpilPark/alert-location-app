package com.toyproject.honeyimleaving;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class BaseApplication extends Application  {

    public static boolean DEBUG = false;

    @Override
    public void onCreate() {
        super.onCreate();
        DEBUG = isDebuggable(this);
    }

    private boolean isDebuggable(Context context) {
        boolean debuggable = false;

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {

        }

        return debuggable;
    }
}
