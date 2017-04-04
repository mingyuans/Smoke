package com.mingyuans.smoke.android;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.mingyuans.smoke.DrawBoxProcess;
import com.mingyuans.smoke.LineInitialProcess;
import com.mingyuans.smoke.Processes;
import com.mingyuans.smoke.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by yanxq on 17/3/31.
 */

public class AndroidProcesses extends Processes {

    public static AndroidProcesses androidDefault() {
        SmokeUncaughtErrorHandler.register();
        AndroidProcesses processes = new AndroidProcesses();
        processes.addCollectorFirst(new LineInitialProcess())
                .addCollector(new DrawBoxProcess())
                .addPrinter(new AndroidConsolePrinter());
        return processes;
    }

    private static Application APPLICATION = null;
    public static synchronized Application getApplication() {
        if (APPLICATION == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                try {
                    Class activityThreadClazz = Class.forName("android.app.ActivityThread");
                    Method getApplicationMethod = activityThreadClazz.getDeclaredMethod("currentApplication");
                    getApplicationMethod.setAccessible(true);
                    APPLICATION = (Application) getApplicationMethod.invoke(null);
                } catch (Throwable throwable) {
                    System.err.println("Smoke. getApplication error. \n" + StringUtil.throwable2String(throwable));
                }
            } else {
                try {
                    Class activityThreadClazz = Class.forName("android.app.ActivityThread");
                    Method instanceMethod = activityThreadClazz.getDeclaredMethod("currentActivityThread");
                    instanceMethod.setAccessible(true);
                    Object instanceObject = instanceMethod.invoke(null);
                    if (instanceObject != null) {
                        Method getApplicationMethod = activityThreadClazz.getDeclaredMethod("getApplication");
                        getApplicationMethod.setAccessible(true);
                        APPLICATION = (Application) getApplicationMethod.invoke(instanceObject);
                    }
                } catch (Throwable throwable) {
                    System.err.println("Smoke. getApplication error. \n" + StringUtil.throwable2String(throwable));
                }
            }
        }
        return APPLICATION;
    }

}
