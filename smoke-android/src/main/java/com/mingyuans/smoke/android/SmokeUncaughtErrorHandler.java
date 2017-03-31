package com.mingyuans.smoke.android;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yanxq on 2017/3/12.
 */

public class SmokeUncaughtErrorHandler implements Thread.UncaughtExceptionHandler {

    private static final String SP_NAME = "smoke";
    private static final String SP_KEY_DISABLE_PRE = "disable_smoke_";

    private Thread.UncaughtExceptionHandler mOrigin;

    private static SmokeUncaughtErrorHandler sInstance;

    public static synchronized boolean register() {
        if (sInstance == null) {
            Thread.UncaughtExceptionHandler origin = Thread.getDefaultUncaughtExceptionHandler();
            sInstance = new SmokeUncaughtErrorHandler(origin);
        }
        Thread.setDefaultUncaughtExceptionHandler(sInstance);
        return true;
    }

    public SmokeUncaughtErrorHandler(Thread.UncaughtExceptionHandler origin) {
        mOrigin = origin;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Smoke.error(e,"Uncaught exception : target thread [{0}-{1}]",t.getId(),t.getName());
        if (isSmokeError(e)) {
            disableVersion();
        }
        if (mOrigin != null) {
            mOrigin.uncaughtException(t,e);
        }
    }

    protected boolean isSmokeError(Throwable throwable) {
        Throwable causeThrowable = throwable;
        while (causeThrowable.getCause() != null) {
            causeThrowable = causeThrowable.getCause();
        }
        String smokePackage = Smoke.class.getPackage().getName();
        StackTraceElement[] elements = causeThrowable.getStackTrace();
        for (StackTraceElement element : elements) {
            String className = element.getClassName();
            String packageName = className.substring(0,className.lastIndexOf('.'));
            if (smokePackage.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    protected SharedPreferences getPreferences() {
        return mContext.getSharedPreferences("smoke",Context.MODE_PRIVATE);
    }

    private void disableVersion() {
        getPreferences().edit()
                .putBoolean(getVersionKey(),true)
                .apply();

        SubSmoke.isDisableVersion = true;
    }

    public static void onAndroidNativeCrash(String message) {
        Smoke.error(message);
        if (sInstance != null) {
            sInstance.disableVersion();
        }
    }

    public static boolean isDisableVersion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return preferences.getBoolean(getVersionKey(),false);
    }

    private static String getVersionKey() {
        return SP_KEY_DISABLE_PRE + BuildConfig.SMOKE_VERSION;
    }
}
