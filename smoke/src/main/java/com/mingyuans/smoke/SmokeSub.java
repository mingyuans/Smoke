package com.mingyuans.smoke;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yanxq on 2017/3/2.
 */

public class SmokeSub implements ISmoke {

    static {
        System.loadLibrary("smoke-lib");
    }

    protected static final int MAX_LINE_LENGTH = 4000;
    protected static boolean isDisableVersion = false;

    protected Context mContext;
    protected int mLogPriority = Log.VERBOSE;
    protected int mExtraMethodElementIndex = 0;
    protected volatile boolean writeEnable = false;
    protected volatile boolean consoleEnable = true;
    protected Smoke.PrintPlugin mPrintPlugin = new DefaultPrintPlugin();
    protected final LinkedList<String> mSubTagList = new LinkedList<String>();

    public SmokeSub(Context context,String subTag,List<String> parentTags) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null!");
        }
        mContext = context.getApplicationContext();

        if (parentTags != null) {
            CollectionUtil.addAll(parentTags,mSubTagList);
        }
        if (!TextUtils.isEmpty(subTag)) {
            mSubTagList.addLast(subTag);
        } else if (CollectionUtil.isEmpty(mSubTagList)) {
            mSubTagList.addFirst(mContext.getPackageName());
        }

        isDisableVersion = SmokeUncaughtErrorHandler.isDisableVersion(mContext);
    }

    public void setExtraMethodOffset(int extraIndex) {
        mExtraMethodElementIndex = extraIndex;
    }

    public void setPrintPlugin(Smoke.PrintPlugin plugin) {
        if (plugin != null) {
            mPrintPlugin = plugin;
        }
    }

    public void setLogPriority(int priority) {
        mLogPriority = priority;
    }

    public void enableConsoleOrWrite(Boolean consoleEnable,Boolean writeEnable) {
        if (consoleEnable != null) {
            this.consoleEnable = consoleEnable;
        }

        if (writeEnable != null) {
            //第一版未完善写文件，先屏蔽
            //// TODO: 17/3/2 yanxq
            this.writeEnable = false;
        }
    }

    @Override
    public void verbose() {
        realPrintln(Log.VERBOSE,null,null);
    }

    @Override
    public void verbose(Object object) {
        String message = StringUtil.toString(object);
        println(Log.VERBOSE,null,message);
    }

    @Override
    public void verbose(String message, Object... args) {
        println(Log.VERBOSE,null,message,args);
    }

    @Override
    public void debug() {
        realPrintln(Log.DEBUG,null,null);
    }

    @Override
    public void debug(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Log.DEBUG,(Throwable)object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Log.DEBUG,null,message);
        }
    }

    @Override
    public void debug(String message, Object... args) {
        println(Log.DEBUG,null,message,args);
    }

    @Override
    public void info() {
        println(Log.INFO,null,null);
    }

    @Override
    public void info(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Smoke.INFO,(Throwable)object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Smoke.INFO,null,message);
        }
    }

    @Override
    public void info(String message, Object... args) {
        println(Smoke.INFO,null,message,args);
    }

    @Override
    public void warn() {
        realPrintln(Smoke.WARN,null,null);
    }

    @Override
    public void warn(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Smoke.WARN,(Throwable)object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Smoke.WARN,null,message);
        }
    }

    @Override
    public void warn(Throwable throwable, String message, Object... args) {
        println(Smoke.WARN,throwable,message,args);
    }

    @Override
    public void warn(String message, Object... args) {
        println(Smoke.WARN,null,message,args);
    }

    @Override
    public void error() {
        realPrintln(Smoke.ERROR,null,null);
    }

    @Override
    public void error(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Smoke.ERROR,(Throwable) object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Smoke.ERROR,null,message);
        }
    }

    @Override
    public void error(String message, Object... args) {
        println(Smoke.ERROR,null,message,args);
    }

    @Override
    public void error(Throwable throwable, String message, Object... args) {
        println(Smoke.ERROR,throwable,message,args);
    }

    @Override
    public void log(int level, String tag, String message, Object... args) {
        println(level,tag,null,message,args);
    }

    @Override
    public void xml(int level, String tag, String xml) {
        String message = StringUtil.xml2String(xml);
        println(level,tag,null,message);
    }

    @Override
    public void xml(int level, String xml) {
        String message = StringUtil.xml2String(xml);
        println(level,null,message);
    }

    @Override
    public void json(int level, String tag, String json) {
        String message = StringUtil.json2String(json);
        println(level,tag,null,message);
    }

    @Override
    public void json(int level, String json) {
        String message = StringUtil.json2String(json);
        println(level,null,message);
    }

    public SmokeSub newSub(String sub) {
        SmokeSub newSub = clone();
        newSub.mSubTagList.addLast(sub);
        return newSub;
    }

    public SmokeSub newSub(String sub, Smoke.PrintPlugin plugin) {
        SmokeSub newSub = clone();
        if (plugin != null) {
            newSub.setPrintPlugin(plugin);
        }
        if (!TextUtils.isEmpty(sub)) {
            newSub.mSubTagList.addLast(sub);
        }
        return newSub;
    }

    public void close() {
        if (CollectionUtil.isEmpty(mSubTagList)) {
            jniClose();
        } else {
            warn("Please call Smoke.close() to close.");
        }
    }

    public SmokeSub clone() {
        SmokeSub newSub = new SmokeSub(mContext,"",mSubTagList);
        newSub.setExtraMethodOffset(0);
        newSub.setLogPriority(mLogPriority);
        newSub.enableConsoleOrWrite(consoleEnable, writeEnable);
        newSub.setPrintPlugin(mPrintPlugin);
        return newSub;
    }

    protected void println(int level, Throwable throwable, String message, Object... args) {
        if (level >= mLogPriority && !isDisableVersion) {
            String[] finalLines = generateRealPrintLines(level,throwable,message,args);
            String firstTAG = mSubTagList.getFirst();
            realPrintln(level,firstTAG,finalLines);
        } else if (isDisableVersion && consoleEnable && Smoke.DEBUG >= mLogPriority) {
            String firstTAG = mSubTagList.getFirst();
            Log.d(firstTAG,"An exception has occurred and Smoke is turned off!");
        }
    }

    protected void println(int level, String tag,Throwable throwable, String message, Object... args) {
        if (level >= mLogPriority && !isDisableVersion) {
            String[] finalLines = generateRealPrintLines(level,throwable,message,args);
            realPrintln(level,tag,finalLines);
        } else if (isDisableVersion && consoleEnable && Smoke.DEBUG >= mLogPriority) {
            Log.d(tag,"An exception has occurred and the function is turned off!");
        }
    }

    protected String[] generateRealPrintLines(int level, Throwable throwable, String message, Object... args) {
        StackTraceElement traceElement = getTraceElement();
        Smoke.LogInfo logInfo = new Smoke.LogInfo(level,traceElement,message,args,throwable);
        logInfo.subTags = CollectionUtil.clone(mSubTagList);

        String[] finalLines = null;
        try {
            if (mPrintPlugin != null) {
                finalLines = mPrintPlugin.toString(logInfo);
            }
        } catch (Throwable error) {
            finalLines = new String[]{message};
        }
        return finalLines;
    }

    protected void realPrintln(int level, String tag, String[] finalPrintMessages) {
        if (finalPrintMessages != null && finalPrintMessages.length > 0) {
            if (writeEnable) {
                jniPrintln(level,tag,finalPrintMessages);
            } else if (consoleEnable) {
                consolePrint(level,tag,finalPrintMessages);
            }
        }
    }

    protected void consolePrint(int level,String tag,String[] messages) {
        if (messages == null || messages.length == 0) {
            return;
        }

        for (String finalPrintMessage : messages) {
            //检查是否过长
            if (finalPrintMessage.length() > MAX_LINE_LENGTH) {
                int splits = finalPrintMessage.length() / MAX_LINE_LENGTH + 1;
                for (int i = 0, startIndex = 0; i < splits; i++) {
                    int endIndex = startIndex + MAX_LINE_LENGTH > finalPrintMessage.length()?
                            finalPrintMessage.length(): startIndex + MAX_LINE_LENGTH;
                    String lineText = (startIndex == 0? "" : "  ") + finalPrintMessage.substring(startIndex,endIndex);
                    Log.println(level,tag,lineText);
                    startIndex = endIndex;
                }
            } else {
                Log.println(level,tag,finalPrintMessage);
            }
        }
    }

    protected StackTraceElement getTraceElement() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement element = elements[mExtraMethodElementIndex + 6];
        return element;
    }

    protected native static void jniPrintln(int level, String tag,String[] message);

    protected native static void jniConsoleEnable(boolean enable);

    public native static void jniOpen(String fileDir,String cacheDir,String namePrefix);

    protected native static void jniClose();

}
