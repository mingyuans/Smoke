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
    private String mLogFileSuffix = ".sm";

    /**
     * 默认文件夹路径: context.getExternalCacheDir;
     * 默认文件夹命名& LOG 文件前缀: "smoke"
     */
    public AndroidFilePrinter() {

    }

    /**
     * 默认文件夹路径: context.getExternalCacheDir;
     * @param namePrefix : 文件夹命名 & LOG 文件前缀
     */
    public AndroidFilePrinter(String namePrefix) {
        mNamePrefix = namePrefix;
    }

    /**
     *
     * @param logDir: 自定义文件夹路径
     * @param namePrefix: 文件夹命名 & LOG 文件前缀
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

    public String getCurrentLogDirPath() {
        return jniGetLogDirPath();
    }

    /**
     * 获取当前日志文件路径
     * @return
     */
    public String getCurrentLogFilePath() {
        return jniCurrentLogFilePath();
    }

    /**
     * 获取 N 天之前的日志文件路径
     * @param timeSpan: N 天之前
     * @return: 日志文件路径
     */
    public String[] getLogFileFromTimespan(int timeSpan) {
        if (timeSpan > 0) {
            return jniGetLogFilesFromTimespan(timeSpan,mNamePrefix);
        }
        return new String[0];
    }

    /*
     * 将缓冲区日志刷入文件保存;
     * 该操作为非阻塞操作;
     *
     * Note: 一般不需要执行,缓冲区内容不会因为进程异常退出而丢失;
     * 进程异常后,缓冲区内容需要下次启动时才能写入当天文件;
     */
    public native void flush();

    /**
     * 将缓冲区日志刷入文件保存;
     * 该操作为阻塞操作;
     *
     * Note: 一般不需要执行,缓冲区内容不会因为进程异常退出而丢失;
     * 进程异常后,缓冲区内容需要下次启动时才能写入当天文件;
     */
    public native void flushSync();

    protected native String[] jniGetLogFilesFromTimespan(int timeSpan,String prefix);

    protected native String jniCurrentLogFilePath();

    protected native String jniGetLogDirPath();

    protected native void jniPrintln(int level, String tag, String[] message);

    protected native void jniOpen(int appendMode,String fileDir,String cacheDir,String namePrefix,String fileSuffix);

    protected native void jniClose();
}
