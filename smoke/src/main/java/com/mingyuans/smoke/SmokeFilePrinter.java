package com.mingyuans.smoke;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

/**
 * Created by yanxq on 17/3/17.
 */

public class SmokeFilePrinter extends Smoke.Process {

    static {
        System.loadLibrary("smoke-lib");
    }

    public static final int APPEND_MODE_ASYNC = 0;
    public static final int APPEND_MODE_SYNC = 1;

    private String mLogDirPath = "";
    private String mNamePrefix = "";

    public SmokeFilePrinter() {

    }

    public SmokeFilePrinter(String name) {
        mNamePrefix = name;
    }

    public SmokeFilePrinter(String logDir, String name) {
        mLogDirPath = logDir;
        mNamePrefix = name;
    }

    @Override
    public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
        jniPrintln(logBean.level,logBean.tag,messages.toArray(new String[messages.size()]));
        return chain.proceed(logBean,messages);
    }

    @Override
    public void open(Context context) {
        if (TextUtils.isEmpty(mNamePrefix)) {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            mNamePrefix = (String) context.getPackageManager().getApplicationLabel(applicationInfo);
        }
        if (TextUtils.isEmpty(mLogDirPath)) {
            mLogDirPath = new File(context.getExternalCacheDir(),mNamePrefix)
                    .getAbsolutePath();
        }
        File cacheDir = new File(context.getCacheDir(),mNamePrefix);
        jniOpen(APPEND_MODE_SYNC,mLogDirPath,cacheDir.getAbsolutePath(),mNamePrefix);
    }

    @Override
    public void close() {
        jniClose();
    }

    protected native void jniPrintln(int level, String tag, String[] message);

    public native void jniOpen(int appendMode,String fileDir,String cacheDir,String namePrefix);

    protected native void jniClose();
}
