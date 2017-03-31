package com.mingyuans.smoke.android;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.mingyuans.smoke.DrawBoxProcess;
import com.mingyuans.smoke.LineInitialProcess;
import com.mingyuans.smoke.Processes;

/**
 * Created by yanxq on 17/3/31.
 */

public class AndroidProcesses extends Processes {

    public static AndroidProcesses createDefault() {
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
            if (Build.VERSION.SDK_INT >= 14) {
                try {

                } catch (Throwable throwable) {

                }
            }
        }

        return APPLICATION;
    }

}
