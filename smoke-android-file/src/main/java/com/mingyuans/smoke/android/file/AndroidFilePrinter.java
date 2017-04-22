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

    /**
     *  Write log file by non-blocked.
     */
    public static final int APPEND_MODE_ASYNC = 0;
    /**
     * Write log file by blocked.
     */
    public static final int APPEND_MODE_SYNC = 1;

    private String mLogDirPath = "";
    private String mNamePrefix = "";
    private String mLogFileSuffix = ".sm";

    /**
     * Default directory: context.getExternalCacheDir;
     * Default directory name and file name prefix: "smoke"
     */
    public AndroidFilePrinter() {

    }

    /**
     * Default directory: context.getExternalCacheDir;
     * @param namePrefix : The LOG directory name and file name prefix
     */
    public AndroidFilePrinter(String namePrefix) {
        mNamePrefix = namePrefix;
    }

    /**
     *
     * @param logDir: Customize the directory path
     * @param namePrefix: file name prefix
     */
    public AndroidFilePrinter(String logDir, String namePrefix) {
        mLogDirPath = logDir;
        mNamePrefix = namePrefix;
    }

    public AndroidFilePrinter setFileSuffix(String suffix) {
        if (!StringUtil.isEmpty(suffix)) {
            if (!suffix.startsWith(".")) {
                suffix = "." + suffix;
            }
            mLogFileSuffix = suffix;
        }
        return this;
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
        jniOpen(APPEND_MODE_ASYNC,mLogDirPath,cacheDir,mNamePrefix,mLogFileSuffix);
    }

    @Override
    public void close() {
        jniClose();
    }

    /**
     * 获得当前日志文件路径
     * @return
     */
    public String getCurrentLogDirPath() {
        return jniGetLogDirPath();
    }

    /**
     * Get the path of current log file.
     * @return
     */
    public String getCurrentLogFilePath() {
        return jniCurrentLogFilePath();
    }

    /**
     * Get the log file path for N days ago
     * @param timeSpan: N days ago
     * @return: The file path
     */
    public String[] getLogFileFromTimespan(int timeSpan) {
        if (timeSpan > 0) {
            return jniGetLogFilesFromTimespan(timeSpan,mNamePrefix);
        }
        return new String[0];
    }

    /*
     * Flush the buffer log into the file.
     * This operation is non-blocked.
     *
     * Note: Generally do not need to call, the buffer will not be lost
     * due to the process of abnormal exit;
     */
    public native void flush();

    /*
     * Flush the buffer log into the file.
     * This operation is blocked.
     *
     * Note: Generally do not need to call, the buffer will not be lost
     * due to the process of abnormal exit;
     */
    public native void flushSync();

    protected native String[] jniGetLogFilesFromTimespan(int timeSpan,String prefix);

    protected native String jniCurrentLogFilePath();

    protected native String jniGetLogDirPath();

    protected native void jniPrintln(int level, String tag, String[] message);

    protected native void jniOpen(int appendMode,String fileDir,String cacheDir,String namePrefix,String fileSuffix);

    protected native void jniClose();
}
