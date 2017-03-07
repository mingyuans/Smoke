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

    protected Context mContext;
    protected int mLogPriority = Log.VERBOSE;
    protected int mExtraMethodElementIndex = 0;
    protected volatile boolean writeEnable = false;
    protected volatile boolean consoleEnable = true;
    protected Smoke.PrintPlugin mPrintPlugin = new DefaultPrintPlugin();
    protected final LinkedList<String> mSubTagList = new LinkedList<String>();

    public SmokeSub(Context context,List<String> parentTags, String subTag) {
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
    }

    public void setExtraMethodElementIndex(int extraIndex) {
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
        println(Log.VERBOSE,null,null);
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
        println(Log.DEBUG,null,null);
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
            println(Log.INFO,(Throwable)object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Log.INFO,null,message);
        }
    }

    @Override
    public void info(String message, Object... args) {
        println(Log.INFO,null,message,args);
    }

    @Override
    public void warn() {
        println(Log.WARN,null,null);
    }

    @Override
    public void warn(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Log.WARN,(Throwable)object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Log.WARN,null,message);
        }
    }

    @Override
    public void warn(Throwable throwable, String message, Object... args) {
        println(Log.WARN,throwable,message,args);
    }

    @Override
    public void warn(String message, Object... args) {
        println(Log.WARN,null,message,args);
    }

    @Override
    public void error() {
        println(Log.ERROR,null,null);
    }

    @Override
    public void error(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Log.ERROR,(Throwable) object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Log.ERROR,null,message);
        }
    }

    @Override
    public void error(String message, Object... args) {
        println(Log.ERROR,null,message,args);
    }

    @Override
    public void error(Throwable throwable, String message, Object... args) {
        println(Log.ERROR,throwable,message,args);
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
        SmokeSub newSub = new SmokeSub(mContext,mSubTagList,"");
        newSub.setExtraMethodElementIndex(mExtraMethodElementIndex);
        newSub.setLogPriority(mLogPriority);
        newSub.enableConsoleOrWrite(consoleEnable, writeEnable);
        newSub.setPrintPlugin(mPrintPlugin);
        return newSub;
    }

    protected void println(int level, Throwable throwable, String message, Object... args) {
        if (level < mLogPriority) return;

        StackTraceElement traceElement = getTraceElement();
        Smoke.LogInfo logInfo = new Smoke.LogInfo(level,traceElement,message,args,throwable);
        logInfo.subTags = CollectionUtil.clone(mSubTagList);

        if (mPrintPlugin != null) {
            String[] finalPrintMessages= mPrintPlugin.toString(logInfo);
            //如果返回 null, PrintPlugin 中自行处理打印事务；
            if (finalPrintMessages != null && finalPrintMessages.length > 0) {
                String firstTAG = mSubTagList.getFirst();
                if (writeEnable) {
                    jniPrintln(level,firstTAG,finalPrintMessages);
                } else if (consoleEnable) {
                    consolePrint(level,firstTAG,finalPrintMessages);
                }
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
                            finalPrintMessage.length() : startIndex + MAX_LINE_LENGTH;
                    String lineText = (startIndex == 0? "" : "  ") + finalPrintMessage.substring(startIndex,endIndex);
                    Log.println(level,tag,lineText);
                    startIndex = startIndex + endIndex;
                }
            } else {
                Log.println(level,tag,finalPrintMessage);
            }
        }
    }

    protected StackTraceElement getTraceElement() {
        Throwable throwable = new Throwable();
        StackTraceElement element = throwable.getStackTrace()[mExtraMethodElementIndex + 3];
        return element;
    }

    protected native static void jniPrintln(int level, String tag,String[] message);

    protected native static void jniConsoleEnable(boolean enable);

    public native static void jniOpen(String fileDir,String cacheDir,String namePrefix);

    protected native static void jniClose();

}
