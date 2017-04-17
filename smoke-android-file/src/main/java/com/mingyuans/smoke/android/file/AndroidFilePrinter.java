package com.mingyuans.smoke.android.file;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Environment;

import com.mingyuans.smoke.Smoke;
import com.mingyuans.smoke.StringUtil;
import com.mingyuans.smoke.android.AndroidProcesses;

import java.io.File;
import java.util.List;

/**
 * Created by yanxq on 2017/4/4.
 */

public class AndroidFilePrinter extends Smoke.Process{
    static {
        System.loadLibrary("smoke-lib");
    }

    public static final int APPEND_MODE_ASYNC = 0;
    public static final int APPEND_MODE_SYNC = 1;

    private String mLogDirPath = "";
    private String mNamePrefix = "";

    public AndroidFilePrinter() {

    }

    public AndroidFilePrinter(String name) {
        mNamePrefix = name;
    }

    public AndroidFilePrinter(String logDir, String name) {
        mLogDirPath = logDir;
        mNamePrefix = name;
    }

    private void makeSureArgsNotNull() {
        Application application = AndroidProcesses.getApplication();
        if (StringUtil.isEmpty(mNamePrefix)) {
            if (application != null) {
                ApplicationInfo applicationInfo = application.getApplicationInfo();
                mNamePrefix = (String) application.getPackageManager().getApplicationLabel(applicationInfo);
            } else {
                mNamePrefix = "smoke";
            }
        }
        if (StringUtil.isEmpty(mLogDirPath)) {
            if (application != null) {
                File cacheDir = application.getExternalCacheDir();
                mLogDirPath = new File(cacheDir,mNamePrefix).getAbsolutePath();
            } else {
                File sdcardDir = Environment.getExternalStorageDirectory();
                mLogDirPath = new File(sdcardDir,mNamePrefix).getAbsolutePath();
            }
        }
    }

    private String getCacheDir() {
        File cacheDir;
        Application application = AndroidProcesses.getApplication();
        if (application != null) {
            File applicationCacheDir = application.getCacheDir();
            cacheDir = new File(applicationCacheDir,mNamePrefix);
        } else {
            File sdcardDir = Environment.getExternalStorageDirectory();
            cacheDir = new File(sdcardDir,mNamePrefix + "-cache");
        }
        return cacheDir.getAbsolutePath();
    }

    @Override
    public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
        jniPrintln(logBean.level,logBean.tag,messages.toArray(new String[messages.size()]));
        return chain.proceed(logBean,messages);
    }

    @Override
    public void open() {
        makeSureArgsNotNull();
        String cacheDir = getCacheDir();
        jniOpen(APPEND_MODE_ASYNC,mLogDirPath,cacheDir,mNamePrefix);
    }

    @Override
    public void close() {
        jniClose();
    }

    public String getCurrentLogDirPath() {
        return jniGetLogDirPath();
    }

    public String getCurrentLogFilePath() {
        return jniCurrentLogFilePath();
    }

    public String[] getLogFileFromTimespan(int timeSpan) {
        if (timeSpan > 0) {
            return jniGetLogFilesFromTimespan(timeSpan,mNamePrefix);
        }
        return new String[0];
    }

    public native void flush();

    public native void flushSync();

    protected native String[] jniGetLogFilesFromTimespan(int timeSpan,String prefix);

    protected native String jniCurrentLogFilePath();

    protected native String jniGetLogDirPath();

    protected native void jniPrintln(int level, String tag, String[] message);

    public native void jniOpen(int appendMode,String fileDir,String cacheDir,String namePrefix);

    protected native void jniClose();
}
